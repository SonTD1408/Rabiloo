package com.namtg.egovernment.service.reply_comment;

import com.google.common.collect.Maps;
import com.namtg.egovernment.dto.object_request.ConfirmCommentDto;
import com.namtg.egovernment.dto.reply_comment.ReplyCommentDto;
import com.namtg.egovernment.dto.response.ResponseCase;
import com.namtg.egovernment.dto.response.ServerResponseDto;
import com.namtg.egovernment.entity.comment.CommentEntity;
import com.namtg.egovernment.entity.notification.NotificationAdminEntity;
import com.namtg.egovernment.entity.reply_comment.ReplyCommentEntity;
import com.namtg.egovernment.entity.user.UserEntity;
import com.namtg.egovernment.enum_common.StatusComment;
import com.namtg.egovernment.enum_common.TypeNotificationAdmin;
import com.namtg.egovernment.repository.reply_comment.ReplyCommentRepository;
import com.namtg.egovernment.service.AmazonService;
import com.namtg.egovernment.service.calendar_delete_comment.CalendarDeleteCommentService;
import com.namtg.egovernment.service.comment.CommentService;
import com.namtg.egovernment.service.like.LikeService;
import com.namtg.egovernment.service.notification.NotificationAdminService;
import com.namtg.egovernment.service.notification.NotificationUserService;
import com.namtg.egovernment.service.reason_denied_comment.ReasonDeniedCommentService;
import com.namtg.egovernment.service.user.UserService;
import com.namtg.egovernment.util.Constant;
import com.namtg.egovernment.util.DateUtils;
import com.namtg.egovernment.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReplyCommentService {

    @Autowired
    private ReplyCommentRepository replyCommentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private NotificationAdminService notificationAdminService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ReasonDeniedCommentService reasonDeniedCommentService;

    @Autowired
    private NotificationUserService notificationUserService;

    @Autowired
    private CalendarDeleteCommentService calendarDeleteCommentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private AmazonService amazonService;

    public Map<Long, List<ReplyCommentEntity>> getMapListReplyCommentByCommentId(List<Long> listCommentId, Long currentUserId) {
        List<ReplyCommentEntity> listReplyComment = replyCommentRepository.getListReplyCommentByListCommentId(listCommentId);
        if (listReplyComment.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Long> listReplyCommentId = listReplyComment
                .stream()
                .map(ReplyCommentEntity::getId)
                .collect(Collectors.toList());
        Map<Long, Date> mapTimeDeleteReplyCommentByReplyCommentId = calendarDeleteCommentService.getMapTimeDeleteReplyCommentByReplyCommentId(listReplyCommentId);
        Map<Long, Boolean> mapIsLikedByReplyCommentId = likeService.getMapIsLikedByReplyCommentId(listReplyCommentId, currentUserId);

        Set<Long> setUserId = listReplyComment
                .stream()
                .flatMap(rc -> Stream.of(rc.getUserId(), rc.getTagUserId()))
                .collect(Collectors.toSet());
        List<UserEntity> listUser = userService.getListUserByListUserId(setUserId);
        Map<Long, String> mapNameUserByUserId = userService.getMapNameUserByUserId(listUser);
        Map<Long, String> mapUrlAvatarUserByUserId = userService.getMapUrlAvatarUserByUserId(listUser);

        List<Long> listReasonDeniedId = listReplyComment
                .stream()
                .map(ReplyCommentEntity::getReasonDeniedId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Map<Long, String> mapContentReasonDenied = reasonDeniedCommentService.getMapContentReasonDeniedByReasonId(listReasonDeniedId);

        listReplyComment.forEach(replyComment -> {
            replyComment.setTimeReplyComment(TimeUtils.convertTimeToString(replyComment.getUpdatedTime()));
            replyComment.setNameUser(mapNameUserByUserId.getOrDefault(replyComment.getUserId(), ""));
            replyComment.setUrlAvatarUser(mapUrlAvatarUserByUserId.get(replyComment.getUserId()));
            replyComment.setNameUserTag(mapNameUserByUserId.getOrDefault(replyComment.getTagUserId(), ""));
            replyComment.setLikedByCurrentUser(mapIsLikedByReplyCommentId.getOrDefault(replyComment.getId(), false));
            if (replyComment.getStatus() == StatusComment.DENIED) {
                replyComment.setReasonDeniedContent(mapContentReasonDenied.getOrDefault(replyComment.getReasonDeniedId(), replyComment.getReasonDeniedOther()));
                replyComment.setTimeDeleteReplyComment(DateUtils.convertDateToString(mapTimeDeleteReplyCommentByReplyCommentId.get(replyComment.getId())));
            }
        });

        return listReplyComment
                .stream()
                .collect(Collectors.groupingBy(ReplyCommentEntity::getCommentId));
    }

    /* userId: người trả lời bình luận */
    @Transactional
    public ServerResponseDto submitReplyComment(ReplyCommentDto replyCommentDto, Long userId) {
        Long replyCommentId = replyCommentDto.getReplyCommentId();
        boolean isUpdate = replyCommentId != null;

        ReplyCommentEntity replyCommentEntity;

        // isUpdate: true -> chỉnh sửa reply comment
        if (isUpdate) {
            replyCommentEntity = replyCommentRepository.findByIdAndIsDeletedFalse(replyCommentId);
            replyCommentEntity.setEdited(true);

            /* delete lịch xóa reply comment */
            calendarDeleteCommentService.deleteByReplyCommentId(replyCommentId);
        } else {
            replyCommentEntity = new ReplyCommentEntity();
            replyCommentEntity.setCommentId(replyCommentDto.getCommentId());
            replyCommentEntity.setNumberHeart(0);
            replyCommentEntity.setCreatedTime(new Date());
        }
        replyCommentEntity.setUpdatedTime(new Date());
        replyCommentEntity.setUserId(userId);
        replyCommentEntity.setContent(replyCommentDto.getContent());
        replyCommentEntity.setStatus(StatusComment.WAITING);

        Long userTagId = replyCommentDto.getUserTagId();
        if (userTagId != null) {
            replyCommentEntity.setTagUserId(userTagId);
        }
        boolean isHaveUserTagEditReplyComment = replyCommentDto.isHaveUserTagEditReplyComment();
        if (!isHaveUserTagEditReplyComment && isUpdate) {
            replyCommentEntity.setTagUserId(null);
        }

        MultipartFile image = replyCommentDto.getImage();
        boolean isHaveImage = replyCommentDto.isHaveImage();

        if (image != null) {
            String nameImage = image.getOriginalFilename();
            replyCommentEntity.setNameImage(nameImage);
            String urlImage = amazonService.uploadFile(image);
            replyCommentEntity.setUrlImage(urlImage);
        } else {
            if (replyCommentEntity.getNameImage() != null && !isHaveImage) {
                replyCommentEntity.setNameImage(null);
            }
        }

        replyCommentEntity = replyCommentRepository.save(replyCommentEntity);

        /* create notification user */
        replyCommentId = replyCommentEntity.getId();
        if (!isUpdate) {
            if (userTagId != null) {
                /* create notification mention_comment ở phía user được tag */
                notificationUserService.createNotificationMentionComment(userTagId, userId, replyCommentId);
            }

            /* create notification reply_comment ở phía user comment */
            Long commentId = replyCommentEntity.getCommentId();
            CommentEntity commentEntity = commentService.getById(commentId);
            Long userReceiveId = commentEntity.getUserId();
            if (userReceiveId != userTagId && userReceiveId != userId) {
                notificationUserService.createNotificationReplyComment(userReceiveId, userId, replyCommentId);
            }
        }
        /*--------------------------*/

        /* create notification admin */
        NotificationAdminEntity notification = new NotificationAdminEntity();
        if (isUpdate) {
            notification.setType(TypeNotificationAdmin.EDIT_COMMENT);
        } else {
            notification.setType(TypeNotificationAdmin.REPLY_COMMENT);
        }
        notification.setUserImpactId(userId);
        notification.setReplyCommentId(replyCommentEntity.getId());
        notification.setCreatedTime(new Date());
        notificationAdminService.createNotificationSubmitComment(notification);
        /*----------------------------*/

        return new ServerResponseDto(ResponseCase.SUCCESS);
    }


    public ReplyCommentEntity getById(Long replyCommentId) {
        ReplyCommentEntity replyCommentEntity = replyCommentRepository.findByIdAndIsDeletedFalse(replyCommentId);
        return replyCommentEntity != null ? replyCommentEntity : null;
    }

    @Transactional
    public ServerResponseDto confirmReplyComment(Long replyCommentId, ConfirmCommentDto confirmCommentDto, Long userConfirmId) {
        ReplyCommentEntity replyCommentEntity = replyCommentRepository.findByIdAndIsDeletedFalse(replyCommentId);
        if (replyCommentEntity == null) {
            return new ServerResponseDto(ResponseCase.ERROR);
        }
        String status = confirmCommentDto.getStatus();
        StatusComment statusReplyComment = null;

        if (status.equals(Constant.APPROVED)) {
            statusReplyComment = StatusComment.APPROVED;
        } else if (status.equals(Constant.DENIED)) {
            statusReplyComment = StatusComment.DENIED;
            Long reasonDeniedId = confirmCommentDto.getReasonDeniedId();
            if (reasonDeniedId != 0) {
                replyCommentEntity.setReasonDeniedId(reasonDeniedId);
            } else {
                String contentReasonDeniedOther = confirmCommentDto.getContentReasonDeniedOther();
                replyCommentEntity.setReasonDeniedOther(contentReasonDeniedOther);
            }

            /* create lịch xóa reply comment */
            calendarDeleteCommentService.createCalendar(null, replyCommentId, confirmCommentDto.getNumberDaysDelete());

            /* xóa thông báo phía user liên quan đến replyCommentId này, (type: REPLY_COMMENT and MENTION_COMMENT) */
            notificationUserService.deleteNotificationWhenReplyCommentDenied(replyCommentId);
        }
        replyCommentEntity.setStatus(statusReplyComment);
        replyCommentEntity.setUserConfirmId(userConfirmId);

        replyCommentRepository.save(replyCommentEntity);

        /* create notification confirm_comment phía user comment */
        Long userReceiveId = replyCommentEntity.getUserId();
        Long postId = commentService.getPostIdByCommentId(replyCommentEntity.getCommentId());
        notificationUserService.createNotificationConfirmComment(statusReplyComment, userReceiveId, null, replyCommentId);
        /*--------------------------------------------*/

        return new ServerResponseDto(ResponseCase.SUCCESS);
    }

    public String getReasonDeniedContent(Long reasonDeniedId, String reasonDeniedOther) {
        if (reasonDeniedId == null) {
            return reasonDeniedOther;
        } else {
            return reasonDeniedCommentService.getContentReasonById(reasonDeniedId);
        }
    }

    public Map<Long, String> getMapTitlePostByReplyCommentId(List<Long> listReplyCommentId) {
        if (listReplyCommentId.isEmpty()) {
            return Collections.emptyMap();
        }
        List<ReplyCommentEntity> listReplyComment = replyCommentRepository.findByIdIn(listReplyCommentId);
        Map<Long, Long> mapCommentIdByReplyCommentId = listReplyComment
                .stream()
                .collect(Collectors.toMap(ReplyCommentEntity::getId, ReplyCommentEntity::getCommentId));

        List<Long> listCommentId = listReplyComment
                .stream()
                .map(ReplyCommentEntity::getCommentId)
                .collect(Collectors.toList());
        Map<Long, String> mapTitlePostByCommentId = commentService.getMapTitlePostByCommentId(listCommentId);

        Map<Long, String> mapResult = Maps.newHashMapWithExpectedSize(listReplyCommentId.size());
        listReplyCommentId.forEach(replyCommentId -> {
            mapResult.put(replyCommentId, mapTitlePostByCommentId.get(mapCommentIdByReplyCommentId.get(replyCommentId)));
        });
        return mapResult;
    }

    public ServerResponseDto deleteReplyComment(Long replyCommentId) {
        ReplyCommentEntity replyCommentEntity = replyCommentRepository.findByIdAndIsDeletedFalse(replyCommentId);
        if (replyCommentEntity == null) {
            return new ServerResponseDto(ResponseCase.ERROR);
        }
        replyCommentEntity.setDeleted(true);
        replyCommentRepository.save(replyCommentEntity);

        /* delete lịch xóa reply comment */
        calendarDeleteCommentService.deleteByReplyCommentId(replyCommentId);

        return new ServerResponseDto(ResponseCase.SUCCESS);
    }

    public void deleteListReplyCommentByCommentId(Long commentId) {
        if (commentId == null) {
            return;
        }
        List<ReplyCommentEntity> listReplyComment = replyCommentRepository.findByCommentIdAndIsDeletedFalse(commentId);
        listReplyComment.forEach(replyComment -> replyComment.setDeleted(true));
        replyCommentRepository.saveAll(listReplyComment);
    }

    public Map<Long, List<ReplyCommentEntity>> getMapListReplyCommentApprovedByCommentId(List<Long> listCommentId) {
        if (listCommentId.isEmpty()) {
            return Collections.emptyMap();
        }
        List<ReplyCommentEntity> listReplyCommentApproved = replyCommentRepository.getListReplyCommentApprovedByListCommentId(listCommentId);
        if (listReplyCommentApproved.isEmpty()) {
            return Collections.emptyMap();
        }

        Set<Long> setUserId = listReplyCommentApproved
                .stream()
                .flatMap(rc -> Stream.of(rc.getUserId(), rc.getTagUserId()))
                .collect(Collectors.toSet());
        Map<Long, String> mapNameUserByUserId = userService.getMapNameUserByUserId(setUserId);

        listReplyCommentApproved.forEach(replyComment -> {
            replyComment.setTimeReplyComment(TimeUtils.convertTimeToString(replyComment.getUpdatedTime()));
            replyComment.setNameUser(mapNameUserByUserId.getOrDefault(replyComment.getUserId(), ""));
            replyComment.setNameUserTag(mapNameUserByUserId.getOrDefault(replyComment.getTagUserId(), ""));
        });

        return listReplyCommentApproved
                .stream()
                .collect(Collectors.groupingBy(ReplyCommentEntity::getCommentId));
    }

    @Transactional
    public void deleteByListId(List<Long> listReplyCommentIdDeleted) {
        if (listReplyCommentIdDeleted.isEmpty()) {
            return;
        }
        replyCommentRepository.deleteByListId(listReplyCommentIdDeleted);
    }

    public void plusHeart(Long replyCommentId, int numberHeart) {
        ReplyCommentEntity replyCommentEntity = replyCommentRepository.findByIdAndIsDeletedFalse(replyCommentId);
        replyCommentEntity.setNumberHeart(replyCommentEntity.getNumberHeart() + numberHeart);
        replyCommentRepository.save(replyCommentEntity);
    }
}
