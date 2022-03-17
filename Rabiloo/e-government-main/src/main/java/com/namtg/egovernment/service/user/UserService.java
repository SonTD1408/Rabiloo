package com.namtg.egovernment.service.user;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.namtg.egovernment.dto.EmailTemplate;
import com.namtg.egovernment.dto.response.ResponseCase;
import com.namtg.egovernment.dto.response.ServerResponseDto;
import com.namtg.egovernment.dto.user.UserDetailResponse;
import com.namtg.egovernment.dto.user.UserDto;
import com.namtg.egovernment.dto.user.UserIdAndNameRoleInterface;
import com.namtg.egovernment.entity.actionable.ActionableEntity;
import com.namtg.egovernment.entity.token.TokenEntity;
import com.namtg.egovernment.entity.user.RoleEntity;
import com.namtg.egovernment.entity.user.UserEntity;
import com.namtg.egovernment.repository.user.UserRepository;
import com.namtg.egovernment.service.AmazonService;
import com.namtg.egovernment.service.EmailService;
import com.namtg.egovernment.service.actionable.ActionableService;
import com.namtg.egovernment.service.token.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Value("${host}")
    public String host;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ActionableService actionableService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AmazonService amazonService;

    public UserEntity findByEmailAndPassword(String email, String password) {
        UserEntity userEntity = userRepository.findByEmailToCheckLogin(email);
        if (userEntity == null) {
            return null;
        }
        boolean isPasswordTrue = passwordEncoder.matches(password, userEntity.getPassword());
        return isPasswordTrue ? userEntity : null;
    }

    public List<UserEntity> getListUserByListUserId(Collection<Long> listUserId) {
        if (listUserId.isEmpty()) {
            return Collections.emptyList();
        }
        return userRepository.findByIdInAndIsDeletedFalse(listUserId);
    }

    public Map<Long, String> getMapNameUserByUserId(Collection<Long> listUserId) {
        if (listUserId.isEmpty()) {
            return Collections.emptyMap();
        }
        List<UserEntity> listUser = getListUserByListUserId(listUserId);
        return getMapNameUserByUserId(listUser);
    }

    public Map<Long, String> getMapNameUserByUserId(List<UserEntity> listUser) {
        if (listUser.isEmpty()) {
            return Collections.emptyMap();
        }
        return listUser
                .stream()
                .collect(Collectors.toMap(UserEntity::getId, UserEntity::getName));
    }

    public Map<Long, String> getMapUrlAvatarUserByUserId(List<UserEntity> listUser) {
        if (listUser.isEmpty()) {
            return Collections.emptyMap();
        }
        return listUser
                .stream()
                .collect(HashMap::new, (m, v) -> m.put(v.getId(), v.getUrlAvatar()), HashMap::putAll);
    }

    public Page<UserEntity> getPage(String keyword, String typeActionableFilter, Pageable pageable) {
        Page<UserEntity> pageResult = userRepository.getPage(keyword, pageable);

        List<Long> listUserId = pageResult.getContent()
                .stream()
                .map(UserEntity::getId)
                .collect(Collectors.toList());
        Map<Long, ActionableEntity> mapActionableByUserId = actionableService.getMapActionableByUserId(listUserId);

        List<UserEntity> listResult = Lists.newArrayListWithCapacity(pageResult.getSize());

        if ("all".equals(typeActionableFilter)) {
            listResult = pageResult.getContent();
        } else if ("manage_post".equals(typeActionableFilter)) {
            listResult = pageResult.getContent()
                    .stream()
                    .filter(user -> mapActionableByUserId.get(user.getId()).isManagePost())
                    .collect(Collectors.toList());
        } else if ("manage_comment_post".equals(typeActionableFilter)) {
            listResult = pageResult.getContent()
                    .stream()
                    .filter(user -> mapActionableByUserId.get(user.getId()).isManageCommentPost())
                    .collect(Collectors.toList());
        } else if ("manage_conclusion_post".equals(typeActionableFilter)) {
            listResult = pageResult.getContent()
                    .stream()
                    .filter(user -> mapActionableByUserId.get(user.getId()).isManageConclusionPost())
                    .collect(Collectors.toList());
        } else if ("manage_field".equals(typeActionableFilter)) {
            listResult = pageResult.getContent()
                    .stream()
                    .filter(user -> mapActionableByUserId.get(user.getId()).isManageField())
                    .collect(Collectors.toList());
        } else if ("manage_reason_denied_comment".equals(typeActionableFilter)) {
            listResult = pageResult.getContent()
                    .stream()
                    .filter(user -> mapActionableByUserId.get(user.getId()).isManageReasonDeniedComment())
                    .collect(Collectors.toList());
        } else if ("manage_news".equals(typeActionableFilter)) {
            listResult = pageResult.getContent()
                    .stream()
                    .filter(user -> mapActionableByUserId.get(user.getId()).isManageNews())
                    .collect(Collectors.toList());
        }
        listResult.forEach(user -> {
            user.setActionableEntity(mapActionableByUserId.get(user.getId()));
        });

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), listResult.size());

        return new PageImpl<>(listResult.subList(start, end), pageable, listResult.size());
    }

    @Transactional
    public ServerResponseDto createOrUpdate(UserDto userDto) {
        ServerResponseDto objectUserEntity = convertEntityToDto(userDto);
        int code = objectUserEntity.getStatus().code;
        if (code == 1600) {
            return new ServerResponseDto(ResponseCase.EMAIL_EXIST);
        } else if (code == 4) {
            return new ServerResponseDto(ResponseCase.ERROR);
        }
        UserEntity userEntity = (UserEntity) objectUserEntity.getData();

        /* set Roles */
        RoleEntity roleEntity = roleService.getById(userDto.getRoleId());
        if (roleEntity == null) {
            return new ServerResponseDto(ResponseCase.ERROR);
        }
        Set<RoleEntity> setRoles = Sets.newHashSet(roleEntity);
        userEntity.setRoles(setRoles);
        /*============*/

        userEntity = userRepository.save(userEntity);
        Long userId = userEntity.getId();

        /* save actionable */
        ActionableEntity actionableEntity;
        Optional<ActionableEntity> optionalActionableEntity = actionableService.getOptionalByUserId(userId);
        if (optionalActionableEntity.isPresent()) {
            actionableEntity = optionalActionableEntity.get();
        } else {
            actionableEntity = new ActionableEntity();
        }
        actionableEntity.setUserId(userId);
        actionableEntity.setManagePost(userDto.isManagePost());
        actionableEntity.setManageCommentPost(userDto.isManageCommentPost());
        actionableEntity.setManageConclusionPost(userDto.isManageConclusionPost());
        actionableEntity.setManageField(userDto.isManageField());
        actionableEntity.setManageReasonDeniedComment(userDto.isManageReasonDeniedComment());
        actionableEntity.setManageNews(userDto.isManageNews());
        actionableService.save(actionableEntity);
        /*==================*/

        return new ServerResponseDto(ResponseCase.SUCCESS);
    }

    private ServerResponseDto convertEntityToDto(UserDto userDto) {
        Long userId = userDto.getId();
        boolean isUpdate = userId != null;

        boolean isEmailExist = false;
        if (!isUpdate) {
            isEmailExist = isEmailExist(userDto.getEmail());
        }
        if (isEmailExist) {
            return new ServerResponseDto(ResponseCase.EMAIL_EXIST);
        }

        UserEntity userEntity;
        if (isUpdate) {
            userEntity = userRepository.findByIdAndIsDeletedFalse(userId);
        } else {
            userEntity = new UserEntity();
            userEntity.setCreatedTime(new Date());
            userEntity.setStatus(1);
            userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        MultipartFile avatar = userDto.getAvatar();
        if (avatar != null) {
            String nameImage = avatar.getOriginalFilename();
            userEntity.setNameAvatar(nameImage);
            String urlImage = amazonService.uploadFile(avatar);
            userEntity.setUrlAvatar(urlImage);
        } else {
            Boolean isHaveAvatar = userDto.getIsHaveAvatar();
            if (isHaveAvatar == null || !isHaveAvatar) {
                userEntity.setNameAvatar(null);
            }
        }
        userEntity.setUpdatedTime(new Date());
        userEntity.setName(userDto.getName());
        userEntity.setEmail(userDto.getEmail());
        userEntity.setPhone(userDto.getPhone());
        userEntity.setAddress(userDto.getAddress());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        try {
            userEntity.setBirthday(sdf.parse(userDto.getBirthday()));
        } catch (ParseException e) {
            return new ServerResponseDto(ResponseCase.ERROR);
        }
        userEntity.setGender(userDto.getGender());
        return new ServerResponseDto(ResponseCase.SUCCESS, userEntity);
    }

    private boolean isEmailExist(String email) {
        return userRepository.countEmail(email) > 0;
    }

    public ServerResponseDto detail(Long id) {
        UserEntity userEntity = userRepository.findByIdAndIsDeletedFalse(id);
        if (userEntity == null) {
            return new ServerResponseDto(ResponseCase.ERROR);
        }
        ActionableEntity actionableEntity = actionableService.findByUserId(userEntity.getId());

        UserDetailResponse userDetailResponse = new UserDetailResponse();
        userDetailResponse.setUserEntity(userEntity);
        userDetailResponse.setActionableEntity(actionableEntity);

        return new ServerResponseDto(ResponseCase.SUCCESS, userDetailResponse);
    }

    public ServerResponseDto changeStatus(Long id) {
        UserEntity userEntity = userRepository.findByIdAndIsDeletedFalse(id);
        if (userEntity == null) {
            return new ServerResponseDto(ResponseCase.ERROR);
        }
        userEntity.setStatus(userEntity.getStatus() == 1 ? 0 : 1);
        userEntity.setUpdatedTime(new Date());
        userRepository.save(userEntity);
        return new ServerResponseDto(ResponseCase.SUCCESS);
    }

    public UserEntity getById(Long userId) {
        return userRepository.getById(userId);
    }

    public ServerResponseDto getNameUser(Long userId) {
        return new ServerResponseDto(ResponseCase.SUCCESS, userRepository.getNameUserByUserId(userId));
    }

    public List<UserEntity> getListAdmin() {
        Set<String> setRoleAdmin = Set.of("GOVERNMENT", "PROVINCE", "DISTRICT", "COMMUNE");
        return userRepository.getListAdmin(setRoleAdmin);
    }

    public ServerResponseDto changePassword(Long adminId, String currentPassword, String newPassword) {
        UserEntity userEntity = userRepository.findByIdAndIsDeletedFalse(adminId);
        if (userEntity == null) {
            return new ServerResponseDto(ResponseCase.ERROR);
        }

        if (!passwordEncoder.matches(currentPassword, userEntity.getPassword())) {
            return new ServerResponseDto(ResponseCase.WRONG_PASSWORD);
        }

        userEntity.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userEntity);
        return new ServerResponseDto(ResponseCase.SUCCESS);
    }

    public List<UserEntity> getListAdminCanReceiveNotificationCommentPost() {
        Set<String> setRoleAdmin = Set.of("GOVERNMENT", "PROVINCE", "DISTRICT", "COMMUNE");
        return userRepository.getListAdminCanReceiveNotificationCommentPost(setRoleAdmin);
    }

    public List<UserEntity> getListAdminCanReceiveNotificationConclusion() {
        Set<String> setRoleAdmin = Set.of("GOVERNMENT", "PROVINCE", "DISTRICT", "COMMUNE");
        return userRepository.getListAdminCanReceiveNotificationConclusion(setRoleAdmin);
    }

    public Map<Long, String> getMapNameRoleByUserId(List<Long> listUserId) {
        Map<Long, String> mapResult = Maps.newHashMapWithExpectedSize(listUserId.size());
        if (listUserId.isEmpty()) {
            return mapResult;
        }
        List<UserIdAndNameRoleInterface> listUserIdAndNameRole = userRepository.getListUserIdAndNameRole(listUserId);
        listUserIdAndNameRole.forEach(obj -> mapResult.put(obj.getUserId(), obj.getNameRole()));
        return mapResult;
    }

    public List<Long> getListAdminIdCanReceiveNotificationApprovePost() {
        Set<String> setRoleAdmin = Set.of("GOVERNMENT", "PROVINCE", "DISTRICT", "COMMUNE");
        return userRepository.getListAdminIdCanReceiveNotificationApprovePost(setRoleAdmin);
    }

    public boolean checkCanCreatePost(Long userId) {
        ActionableEntity actionableEntity = actionableService.findByUserId(userId);
        if (actionableEntity == null) {
            return false;
        }
        return actionableEntity.isManagePost();
    }

    public ServerResponseDto forgotPassword(String email) {
        UserEntity userEntity = userRepository.findByEmailAndIsDeletedFalse(email);
        if (userEntity == null) {
            return new ServerResponseDto(ResponseCase.EMAIL_NOT_FOUND);
        }

        String tokenString = tokenService.generateToken();
        String confirmationUrl = host + "confirmForgotPassword?token=" + tokenString;
        String contentEmailConfirm = "Đổi lại mật khẩu mới tại đường dẫn: " + confirmationUrl;
        String subject = "Quên mật khẩu";
        EmailTemplate emailTemplate = new EmailTemplate(email, subject, contentEmailConfirm);
        emailService.sendMail(emailTemplate);

        TokenEntity tokenEntity = new TokenEntity(userEntity.getId(), tokenString, 4);
        tokenService.save(tokenEntity);
        return new ServerResponseDto(ResponseCase.SUCCESS);
    }

    public boolean confirmForgotPassword(String token) {
        TokenEntity tokenEntity = tokenService.validateToken(token, 4);
        return tokenEntity != null;
    }

    @Transactional
    public ServerResponseDto setPassword(String token, String password) {
        TokenEntity tokenEntity = tokenService.validateToken(token, 4);
        if (tokenEntity == null) {
            return new ServerResponseDto(ResponseCase.ERROR);
        }
        UserEntity userEntity = userRepository.findByIdAndIsDeletedFalse(tokenEntity.getUserId());
        userEntity.setPassword(passwordEncoder.encode(password));
        userRepository.save(userEntity);

        tokenService.deleteToken(tokenEntity);

        return new ServerResponseDto(ResponseCase.SUCCESS);
    }

    public ServerResponseDto getMyProfile(Long currentUserId) {
        UserEntity userEntity = getById(currentUserId);
        userEntity = setInfoUser(userEntity);
        return new ServerResponseDto(ResponseCase.SUCCESS, userEntity);
    }

    private UserEntity setInfoUser(UserEntity userEntity) {
        int genderInt = userEntity.getGender();
        if (genderInt == 1) {
            userEntity.setGenderStr("Nam");
        } else if (genderInt == 2) {
            userEntity.setGenderStr("Nữ");
        } else {
            userEntity.setGenderStr("Khác");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (userEntity.getBirthday() != null) {
            userEntity.setBirthdayStr(sdf.format(userEntity.getBirthday()));
        } else {
            userEntity.setBirthdayStr("");
        }

        return userEntity;
    }

    public ServerResponseDto updateMyProfile(UserDto userDto) {
        ServerResponseDto objectUserEntity = convertEntityToDto(userDto);
        int code = objectUserEntity.getStatus().code;
        if (code == 1600) {
            return new ServerResponseDto(ResponseCase.EMAIL_EXIST);
        } else if (code == 4) {
            return new ServerResponseDto(ResponseCase.ERROR);
        }
        UserEntity userEntity = (UserEntity) objectUserEntity.getData();
        userRepository.save(userEntity);
        return new ServerResponseDto(ResponseCase.SUCCESS);
    }

}
