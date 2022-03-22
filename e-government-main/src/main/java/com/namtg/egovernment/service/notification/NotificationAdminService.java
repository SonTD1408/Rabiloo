package com.namtg.egovernment.service.notification;

import com.google.common.collect.Lists;
import com.namtg.egovernment.dto.object_response.DataNotificationResponse;
import com.namtg.egovernment.dto.response.ResponseCase;
import com.namtg.egovernment.dto.response.ServerResponseDto;
import com.namtg.egovernment.entity.comment.CommentEntity;
import com.namtg.egovernment.entity.discussion_post.PostEntity;
import com.namtg.egovernment.entity.notification.NotificationAdminEntity;
import com.namtg.egovernment.entity.notification.StatusNotificationAdminEntity;
import com.namtg.egovernment.entity.reply_comment.ReplyCommentEntity;
import com.namtg.egovernment.entity.user.UserEntity;
import com.namtg.egovernment.enum_common.StatusComment;
import com.namtg.egovernment.enum_common.TypeNotificationAdmin;
import com.namtg.egovernment.repository.notification.NotificationAdminRepository;
import com.namtg.egovernment.service.comment.CommentService;
import com.namtg.egovernment.service.discussion_post.PostService;
import com.namtg.egovernment.service.reply_comment.ReplyCommentService;
import com.namtg.egovernment.service.user.UserService;
import com.namtg.egovernment.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificationAdminService {
    @Autowired
    private NotificationAdminRepository repository;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ReplyCommentService replyCommentService;

    @Autowired
    private StatusNotificationAdminService statusNotificationAdminService;


    public void createNotificationSubmitComment(NotificationAdminEntity notification) {
        repository.save(notification);

        /* gán thông báo -> list admin nhận được thông báo về phê duyệt bình luận */
        Long notificationId = notification.getId();
        List<UserEntity> listAdminCanReceiveNotificationCommentPost = userService.getListAdminCanReceiveNotificationCommentPost();

        List<StatusNotificationAdminEntity> listStatusNotification = Lists.newArrayListWithExpectedSize(listAdminCanReceiveNotificationCommentPost.size());
        listAdminCanReceiveNotificationCommentPost.forEach(user -> {
            StatusNotificationAdminEntity statusNotification = new StatusNotificationAdminEntity();
            statusNotification.setNotificationId(notificationId);
            statusNotification.setAdminId(user.getId());

            listStatusNotification.add(statusNotification);
        });
        statusNotificationAdminService.saveList(listStatusNotification);
    }

    /* mỗi admin sẽ được nhận số lượng thông báo khác nhau
     * => sẽ lấy listNotificationId tương ứng với mỗi admin trước */
    public List<NotificationAdminEntity> getListNotification(Long adminId) {
        List<StatusNotificationAdminEntity> listStatusNotification = statusNotificationAdminService.getListNotificationByAdminId(adminId);
        if (listStatusNotification.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, Boolean> mapIsReadByNotificationId = listStatusNotification
                .stream()
                .collect(Collectors.toMap(StatusNotificationAdminEntity::getNotificationId, StatusNotificationAdminEntity::isRead));

        List<Long> listNotificationId = listStatusNotification
                .stream()
                .map(StatusNotificationAdminEntity::getNotificationId)
                .collect(Collectors.toList());

        List<NotificationAdminEntity> listNotification = repository.getByListNotificationIdAndOrderByCreatedTimeDesc(listNotificationId);

        if (listNotification.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> listUserId = listNotification
                .stream()
                .map(NotificationAdminEntity::getUserImpactId)
                .collect(Collectors.toList());
        List<UserEntity> listUser = userService.getListUserByListUserId(listUserId);
        Map<Long, String> mapNameUserByUserId = listUser
                .stream()
                .collect(Collectors.toMap(UserEntity::getId, UserEntity::getName));
        Map<Long, String> mapUrlAvatarByUserId = listUser
                .stream()
                .collect(HashMap::new, (m, v) -> m.put(v.getId(), v.getUrlAvatar()), HashMap::putAll);

        List<Long> listCommentId = listNotification
                .stream()
                .map(NotificationAdminEntity::getCommentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Map<Long, String> mapTitlePostByCommentId = commentService.getMapTitlePostByCommentId(listCommentId);


        List<Long> listReplyCommentId = listNotification
                .stream()
                .map(NotificationAdminEntity::getReplyCommentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Map<Long, String> mapTitlePostByReplyCommentId = replyCommentService.getMapTitlePostByReplyCommentId(listReplyCommentId);

        listNotification.forEach(notification -> {
            notification.setTimeNotification(TimeUtils.convertTimeToString(notification.getCreatedTime()));
            String titlePost = null;
            if (notification.getReplyCommentId() != null) {
                titlePost = mapTitlePostByReplyCommentId.get(notification.getReplyCommentId());
            } else if (notification.getCommentId() != null) {
                titlePost = mapTitlePostByCommentId.get(notification.getCommentId());
            }
            notification.setTitlePost(titlePost);
            notification.setNameUserImpact(mapNameUserByUserId.get(notification.getUserImpactId()));
            notification.setUrlAvatarUserImpact(mapUrlAvatarByUserId.get(notification.getUserImpactId()));
            notification.setRead(mapIsReadByNotificationId.get(notification.getId()));
        });
        return listNotification;
    }

    public int getNumberNotificationHavenWatch(Long adminId) {
        return (int) repository.getNumberNotificationHavenWatch(adminId);
    }

    public void setAllNotificationToWatched(Long adminId) {
        List<StatusNotificationAdminEntity> listStatusNotification = statusNotificationAdminService
                .getByAdminIdAndHavenWatch(adminId);

        listStatusNotification.forEach(statusNotification -> {
            statusNotification.setWatched(true);
        });
        statusNotificationAdminService.saveList(listStatusNotification);
    }

    public ServerResponseDto detail(Long notificationId, Long adminId) {
        NotificationAdminEntity notification = repository.getById(notificationId);
        if (notification == null) {
            return new ServerResponseDto(ResponseCase.ERROR);
        }

        /* set notification --> đã đọc */
        statusNotificationAdminService.setNotificationToRead(notificationId, adminId);

        DataNotificationResponse dataResponse = new DataNotificationResponse();
        TypeNotificationAdmin typeNotificationAdmin = notification.getType();
        dataResponse.setTypeNotificationAdmin(typeNotificationAdmin);

        /* case type COMMENT or REPLY_COMMENT */
        if (typeNotificationAdmin == TypeNotificationAdmin.COMMENT || typeNotificationAdmin == TypeNotificationAdmin.REPLY_COMMENT
                || typeNotificationAdmin == TypeNotificationAdmin.EDIT_COMMENT) {
            return responseForDetailComment(notification, dataResponse);
        } else if (typeNotificationAdmin == TypeNotificationAdmin.CREATE_POST) {
            dataResponse.setPostId(notification.getPostId());
            return new ServerResponseDto(ResponseCase.SUCCESS, dataResponse);
        }

        return new ServerResponseDto(ResponseCase.SUCCESS);
    }

    private ServerResponseDto responseForDetailComment(NotificationAdminEntity notification, DataNotificationResponse dataResponse) {
        Long postId = getPostId(notification);
        Long commentId = notification.getCommentId();
        Long replyCommentId = notification.getReplyCommentId();

        PostEntity postEntity = postService.getPostById(postId);
        dataResponse.setPost(postEntity);

        ReplyCommentEntity replyCommentEntity = null;
        Set<Long> listUserId = new HashSet<>();

        // get Reply Comment
        if (replyCommentId != null) {
            replyCommentEntity = replyCommentService.getById(replyCommentId);
            if (replyCommentEntity == null) {
                return new ServerResponseDto(ResponseCase.COMMENT_NOT_FOUND);
            }
            listUserId.add(replyCommentEntity.getUserId());
            listUserId.add(replyCommentEntity.getTagUserId());
            listUserId.add(replyCommentEntity.getUserConfirmId());

            replyCommentEntity.setTimeReplyComment(TimeUtils.convertTimeToString(replyCommentEntity.getUpdatedTime()));
            if (replyCommentEntity.getStatus() == StatusComment.DENIED) {
                String reasonDeniedReplyCommentContent = replyCommentService
                        .getReasonDeniedContent(replyCommentEntity.getReasonDeniedId(), replyCommentEntity.getReasonDeniedOther());
                replyCommentEntity.setReasonDeniedContent(reasonDeniedReplyCommentContent);
            }
            commentId = replyCommentEntity.getCommentId();
        }

        // get Comment
        CommentEntity commentEntity = commentService.getById(commentId);
        if (commentEntity == null) {
            return new ServerResponseDto(ResponseCase.COMMENT_NOT_FOUND);
        }
        listUserId.add(commentEntity.getUserId());
        listUserId.add(commentEntity.getUserConfirmId());

        List<UserEntity> listUser = userService.getListUserByListUserId(listUserId);
        Map<Long, String> mapNameUserByUserId = userService.getMapNameUserByUserId(listUser);
        Map<Long, String> mapUrlAvatarUserByUserId = userService.getMapUrlAvatarUserByUserId(listUser);

        if (replyCommentEntity != null) {
            replyCommentEntity.setNameUser(mapNameUserByUserId.get(replyCommentEntity.getUserId()));
            replyCommentEntity.setUrlAvatarUser(mapUrlAvatarUserByUserId.get(replyCommentEntity.getUserId()));
            replyCommentEntity.setNameUserTag(mapNameUserByUserId.get(replyCommentEntity.getTagUserId()));
            replyCommentEntity.setNameUserConfirm(mapNameUserByUserId.get(replyCommentEntity.getUserConfirmId()));
            dataResponse.setReplyComment(replyCommentEntity);
        }

        commentEntity.setNameUserComment(mapNameUserByUserId.get(commentEntity.getUserId()));
        commentEntity.setUrlAvatarUserComment(mapUrlAvatarUserByUserId.get(commentEntity.getUserId()));
        commentEntity.setNameUserConfirm(mapNameUserByUserId.get(commentEntity.getUserConfirmId()));
        commentEntity.setTimeComment(TimeUtils.convertTimeToString(commentEntity.getUpdatedTime()));
        if (commentEntity.getStatus() == StatusComment.DENIED) {
            String reasonDeniedCommentContent = commentService
                    .getReasonDeniedContent(commentEntity.getReasonDeniedId(), commentEntity.getReasonDeniedOther());
            commentEntity.setReasonDeniedContent(reasonDeniedCommentContent);
        }
        dataResponse.setComment(commentEntity);

        return new ServerResponseDto(ResponseCase.SUCCESS, dataResponse);
    }

    private Long getPostId(NotificationAdminEntity notification) {
        Long replyCommentId = notification.getReplyCommentId();
        Long commentId = notification.getCommentId();

        if (replyCommentId != null) {
            return postService.getPostIdByReplyCommentId(replyCommentId);
        } else {
            return postService.getPostIdByCommentId(commentId);
        }
    }

    public void createNotificationRequestCreatePost(Long postId, Long userImpactId) {
        NotificationAdminEntity notification = new NotificationAdminEntity();
        notification.setUserImpactId(userImpactId);
        notification.setType(TypeNotificationAdmin.CREATE_POST);
        notification.setCreatedTime(new Date());
        notification.setPostId(postId);

        repository.save(notification);

        /* gán thông báo -> list admin nhận được thông báo về phê duyệt bài thảo luận (thêm, sửa, xóa) */
        Long notificationId = notification.getId();
        List<Long> listAdminId = userService.getListAdminIdCanReceiveNotificationApprovePost();
        List<StatusNotificationAdminEntity> listStatusNotification = Lists.newArrayListWithExpectedSize(listAdminId.size());
        listAdminId.forEach(adminId -> {
            StatusNotificationAdminEntity statusNotification = new StatusNotificationAdminEntity();
            statusNotification.setNotificationId(notificationId);
            statusNotification.setAdminId(adminId);

            listStatusNotification.add(statusNotification);
        });

        statusNotificationAdminService.saveList(listStatusNotification);
    }
}
