package com.namtg.egovernment.service.discussion_post;

import com.github.slugify.Slugify;
import com.namtg.egovernment.dto.discussion_post.ConclusionDto;
import com.namtg.egovernment.dto.discussion_post.PostDto;
import com.namtg.egovernment.dto.discussion_post.RequestCreatePostDto;
import com.namtg.egovernment.dto.response.ResponseCase;
import com.namtg.egovernment.dto.response.ServerResponseDto;
import com.namtg.egovernment.entity.comment.CommentEntity;
import com.namtg.egovernment.entity.discussion_post.PostEntity;
import com.namtg.egovernment.entity.user.RoleEntity;
import com.namtg.egovernment.repository.discussion_post.PostRepository;
import com.namtg.egovernment.service.comment.CommentService;
import com.namtg.egovernment.service.notification.NotificationAdminService;
import com.namtg.egovernment.service.user.RoleService;
import com.namtg.egovernment.service.user.UserService;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PostService {
    @Autowired
    private CommentService commentService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostRoleCommentService postRoleCommentService;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private NotificationAdminService notificationAdminService;

    public Page<PostEntity> getPage(String keyword, Pageable pageable) {
        Page<PostEntity> result = postRepository.getPage(keyword, pageable);
        List<Long> listPostId = result.getContent()
                .stream()
                .map(PostEntity::getId)
                .collect(Collectors.toList());
        List<Long> listCreatorId = result.getContent()
                .stream()
                .map(PostEntity::getCreatedByUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Map<Long, String> mapNameRoleByUserId = userService.getMapNameRoleByUserId(listCreatorId);

        Map<Long, List<RoleEntity>> mapListRoleByPostId = postRoleCommentService.getMapListRoleByPostId(listPostId);
        Map<Long, String> mapCreatorByUserId = userService.getMapNameUserByUserId(listCreatorId);

        result.forEach(post -> {
            Long creatorId = post.getCreatedByUserId();
            post.setListRole(mapListRoleByPostId.get(post.getId()));
            post.setNameCreator(mapCreatorByUserId.get(creatorId));
            post.setNameRoleCreator(mapNameRoleByUserId.get(creatorId));
        });
        return result;
    }

    @Transactional
    public ServerResponseDto save(Long creatorId, PostDto postDto) throws ParseException {
        Long id = postDto.getId();
        boolean isUpdate = id != null;

        PostEntity postEntity;
        boolean isPostExist;
        if (isUpdate) {
            postEntity = postRepository.findByIdAndIsDeletedFalse(id);
            isPostExist = isPostExist(postDto.getTitle(), postEntity.getId());
            if (!postEntity.isApproved()) {
                postEntity.setApprovedByUserId(creatorId);
                postEntity.setApprovedTime(new Date());
                postEntity.setApproved(true);
            }
            postEntity.setUpdatedByUserId(creatorId);
            postEntity.setUpdatedTime(new Date());
        } else {
            isPostExist = isPostExist(postDto.getTitle());
            postEntity = new PostEntity();
            postEntity.setNumberView(0);
            postEntity.setCreatedByUserId(creatorId);
            postEntity.setCreatedTime(new Date());
        }
        if (isPostExist) {
            return new ServerResponseDto(ResponseCase.POST_EXIST);
        }

        postEntity.setFieldId(postDto.getFieldId());
        postEntity.setTitle(postDto.getTitle());
        postEntity.setSeo(new Slugify().slugify(postDto.getTitle()));
        postEntity.setContent(postDto.getContent());
        postEntity.setTarget(postDto.getTarget());
        SimpleDateFormat spf = new SimpleDateFormat("yyyy/MM/dd");
        postEntity.setClosingDeadline(spf.parse(postDto.getClosingDeadline()));
        postEntity.setApproved(true);

        String stringListRoleId = postDto.getListRoleId();
        if (stringListRoleId != null) {
            List<String> listRoleIdStr = Arrays.asList(stringListRoleId.split(","));
            Set<Long> listRoleId = listRoleIdStr
                    .stream()
                    .map(Long::parseLong)
                    .collect(Collectors.toSet());
            postRepository.save(postEntity);
            postRoleCommentService.save(listRoleId, postEntity.getId());
        } else {
            postRepository.save(postEntity);
        }

        return new ServerResponseDto(ResponseCase.SUCCESS);
    }

    private boolean isPostExist(String title) {
        return postRepository.countPostExist(title) > 0;
    }

    private boolean isPostExist(String title, Long postId) {
        return postRepository.countPostExist(title, postId) > 0;
    }

    public ServerResponseDto detail(Long postId) {
        PostEntity postEntity = postRepository.findByIdAndIsDeletedFalse(postId);
        if (postEntity == null) {
            return new ServerResponseDto(ResponseCase.ERROR);
        }

        List<Long> listUserId = Stream.of(postEntity.getCreatedByUserId(), postEntity.getApprovedByUserId())
                .collect(Collectors.toList());
        Map<Long, String> mapNameUserByUserId = userService.getMapNameUserByUserId(listUserId);
        Map<Long, String> mapNameRoleByUserId = userService.getMapNameRoleByUserId(listUserId);

        postEntity.setNameCreator(mapNameUserByUserId.get(postEntity.getCreatedByUserId()));
        postEntity.setNameApprover(mapNameUserByUserId.get(postEntity.getApprovedByUserId()));
        postEntity.setNameRoleCreator(mapNameRoleByUserId.get(postEntity.getCreatedByUserId()));
        postEntity.setNameRoleApprover(mapNameRoleByUserId.get(postEntity.getApprovedByUserId()));

        if (postEntity.getConclude() != null) {
            postEntity.setConcludeParse(Jsoup.parse(postEntity.getConclude()).text());
        }
        List<RoleEntity> listRole = postRoleCommentService.getListRole(postId);
        postEntity.setListRole(listRole);

        return new ServerResponseDto(ResponseCase.SUCCESS, postEntity);
    }

    @Transactional
    public ServerResponseDto delete(Long postId) {
        PostEntity postEntity = postRepository.findByIdAndIsDeletedFalse(postId);
        if (postEntity == null) {
            return new ServerResponseDto(ResponseCase.ERROR);
        }
        postEntity.setDeleted(true);
        postRepository.save(postEntity);

        postRoleCommentService.deleteByPostId(postId);
        return new ServerResponseDto(ResponseCase.SUCCESS);
    }

    public Page<PostEntity> getPageForUser(String keyword, String orderBy, Long fieldId, Pageable pageable) {
        List<PostEntity> listResult = postRepository.getListForUser(keyword, fieldId);
        if ("lasted".equals(orderBy)) {
            listResult = listResult.stream()
                    .sorted(Comparator.comparing(PostEntity::getCreatedTime).reversed())
                    .collect(Collectors.toList());
        } else if ("view".equals(orderBy)) {
            listResult = listResult.stream()
                    .sorted(Comparator.comparing(PostEntity::getNumberView).reversed())
                    .collect(Collectors.toList());
        } else if ("solved".equals(orderBy)) {
            listResult = listResult.stream()
                    .filter(p -> p.getConclude() != null)
                    .collect(Collectors.toList());
        } else if ("unsolved".equals(orderBy)) {
            listResult = listResult.stream()
                    .filter(p -> p.getConclude() == null)
                    .collect(Collectors.toList());
        }

        List<Long> listPostId = listResult
                .stream()
                .map(PostEntity::getId)
                .collect(Collectors.toList());
        Map<Long, Long> mapNumberCommentByPostId = commentService.getMapNumberCommentByPostId(listPostId);

        List<Long> listCreatorId = listResult
                .stream()
                .map(PostEntity::getCreatedByUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Map<Long, String> mapCreatorByPostId = userService.getMapNameUserByUserId(listCreatorId);

        listResult.forEach(post -> {
            post.setNameCreator(mapCreatorByPostId.get(post.getCreatedByUserId()));
            post.setNumberComment(mapNumberCommentByPostId.getOrDefault(post.getId(), 0L));
        });

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), listResult.size());

        return new PageImpl<>(listResult.subList(start, end), pageable, listResult.size());
    }

    public PostEntity findBySeoAndIsDeletedFalse(String seo) {
        return postRepository.findBySeoAndIsDeletedFalse(seo);
    }

    public PostEntity detailForAdmin(String seo) {
        PostEntity postEntity = postRepository.findBySeoAndIsDeletedFalse(seo);
        List<CommentEntity> listCommentApproved = commentService.getListCommentApprovedByPostId(postEntity.getId());
        postEntity.setListComment(listCommentApproved);
        return postEntity;
    }

    public ServerResponseDto detailForUser(Long postId, Long currentUserId) {
        PostEntity postEntity = postRepository.findByIdAndIsDeletedFalse(postId);
        if (postEntity == null) {
            return new ServerResponseDto(ResponseCase.ERROR);
        }

        /* set list role can comment */
        List<RoleEntity> listRole = postRoleCommentService.getListRole(postId);
        postEntity.setListRole(listRole);

        /* set list comment */
        List<CommentEntity> listComment = commentService.getListCommentByPostId(postEntity.getId(), currentUserId);
        postEntity.setListComment(listComment);

        return new ServerResponseDto(ResponseCase.SUCCESS, postEntity);
    }

    public PostEntity getPostLatest() {
        return postRepository.getPostLatest();
    }

    public ServerResponseDto plusView(Long postId) {
        PostEntity postEntity = postRepository.findByIdAndIsDeletedFalse(postId);
        if (postEntity == null) {
            return new ServerResponseDto(ResponseCase.ERROR);
        }
        postEntity.setNumberView(postEntity.getNumberView() + 1);
        postRepository.save(postEntity);

        return new ServerResponseDto(ResponseCase.SUCCESS);
    }

    public Map<Long, String> getMapTitlePostByPostId(Collection<Long> listPostId) {
        if (listPostId.isEmpty()) {
            return Collections.emptyMap();
        }
        List<PostEntity> listPost = postRepository.findByIdInAndIsDeletedFalse(listPostId);
        return listPost
                .stream()
                .collect(Collectors.toMap(PostEntity::getId, PostEntity::getTitle));
    }

    public PostEntity getPostById(Long postId) {
        return postRepository.findByIdAndIsDeletedFalse(postId);
    }

    public Long getPostIdByReplyCommentId(Long replyCommentId) {
        if (replyCommentId == null) {
            return null;
        }
        return postRepository.getPostIdByReplyCommentId(replyCommentId);
    }

    public Long getPostIdByCommentId(Long commentId) {
        if (commentId == null) {
            return null;
        }
        return commentService.getPostIdByCommentId(commentId);
    }

    public ServerResponseDto saveConclusion(ConclusionDto conclusionDto) {
        PostEntity postEntity = postRepository.findByIdAndIsDeletedFalse(conclusionDto.getPostId());
        postEntity.setConclude(conclusionDto.getConclusion());
        postRepository.save(postEntity);
        return new ServerResponseDto(ResponseCase.SUCCESS);
    }

    public List<PostEntity> findByIsDeletedFalse() {
        return postRepository.findByIsDeletedFalse();
    }

    public List<PostEntity> getListTop5Post() {
        return postRepository.getListTop5Post();
    }

    public Long getFieldIdByPostId(Long postId) {
        PostEntity postEntity = postRepository.findByIdAndIsDeletedFalse(postId);
        return postEntity.getFieldId();
    }

    @Transactional
    public ServerResponseDto requestCreate(RequestCreatePostDto postDto, Long creatorId) throws ParseException {
        PostEntity postEntity = new PostEntity();
        boolean isCanCreatePost = postDto.isCanCreatePost();
        if (isCanCreatePost) {
            SimpleDateFormat spf = new SimpleDateFormat("yyyy/MM/dd");
            postEntity.setClosingDeadline(spf.parse(postDto.getClosingDeadline()));
        }
        postEntity.setApproved(isCanCreatePost);
        postEntity.setFieldId(postDto.getFieldId());
        postEntity.setTitle(postDto.getTitle());
        postEntity.setSeo(new Slugify().slugify(postDto.getTitle()));
        postEntity.setContent(postDto.getContent());
        postEntity.setTarget(postDto.getTarget());
        postEntity.setNumberView(0);
        postEntity.setCreatedByUserId(creatorId);
        postEntity.setCreatedFromUser(true);
        postEntity.setCreatedTime(new Date());
        postEntity = postRepository.save(postEntity);

        Long postId = postEntity.getId();
        Set<Long> listRoleId = roleService.getSetAllRoleId();
        postRoleCommentService.saveWithAllRole(listRoleId, postId);

        // nếu không có quyền quản lý post, thì tạo thông báo gửi yêu cầu tạo phía admin
        if (!isCanCreatePost) {
            notificationAdminService.createNotificationRequestCreatePost(postId, creatorId);
        }

        return new ServerResponseDto(ResponseCase.SUCCESS);
    }
}
