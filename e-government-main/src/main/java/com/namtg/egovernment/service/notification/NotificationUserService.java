package com.namtg.egovernment.service.notification;

import com.namtg.egovernment.dto.object_response.DataForDetailPost;
import com.namtg.egovernment.dto.response.ResponseCase;
import com.namtg.egovernment.dto.response.ServerResponseDto;
import com.namtg.egovernment.entity.comment.CommentEntity;
import com.namtg.egovernment.entity.notification.NotificationUserEntity;
import com.namtg.egovernment.entity.reply_comment.ReplyCommentEntity;
import com.namtg.egovernment.entity.user.UserEntity;
import com.namtg.egovernment.enum_common.StatusComment;
import com.namtg.egovernment.enum_common.TypeNotificationUser;
import com.namtg.egovernment.repository.notification.NotificationUserRepository;
import com.namtg.egovernment.service.comment.CommentService;
import com.namtg.egovernment.service.discussion_post.PostService;
import com.namtg.egovernment.service.reply_comment.ReplyCommentService;
import com.namtg.egovernment.service.user.UserService;
import com.namtg.egovernment.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class NotificationUserService {

    @Autowired
    private NotificationUserRepository notificationUserRepository;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ReplyCommentService replyCommentService;

    @Autowired
    private UserService userService;

    public void createNotificationConfirmComment(StatusComment statusComment, Long userReceiveId, Long commentId, Long replyCommentId) {
        NotificationUserEntity notificationUser = createNotificationUserCommon(userReceiveId);
        notificationUser.setStatusComment(statusComment);
        notificationUser.setType(TypeNotificationUser.CONFIRM_COMMENT);
        notificationUser.setCommentId(commentId);
        notificationUser.setReplyCommentId(replyCommentId);

        notificationUserRepository.save(notificationUser);
    }

    public void createNotificationMentionComment(Long userTagId, Long userCommentId, Long replyCommentId) {
        NotificationUserEntity notificationUser = createNotificationUserCommon(userTagId);
        notificationUser.setType(TypeNotificationUser.MENTION_COMMENT);
        notificationUser.setUserCommentId(userCommentId);
        notificationUser.setReplyCommentId(replyCommentId);

        notificationUserRepository.save(notificationUser);
    }

    public void createNotificationReplyComment(Long userReceiveId, Long userCommentId, Long replyCommentId) {
        NotificationUserEntity notificationUser = createNotificationUserCommon(userReceiveId);
        notificationUser.setUserReceiveId(userReceiveId);
        notificationUser.setType(TypeNotificationUser.REPLY_COMMENT);
        notificationUser.setUserCommentId(userCommentId);
        notificationUser.setReplyCommentId(replyCommentId);

        notificationUserRepository.save(notificationUser);
    }

    public NotificationUserEntity createNotificationUserCommon(Long userReceiveId) {
        NotificationUserEntity notificationUser = new NotificationUserEntity();
        notificationUser.setUserReceiveId(userReceiveId);
        notificationUser.setCreatedTime(new Date());

        return notificationUser;
    }

    public List<NotificationUserEntity> getList(Long userId) {
        List<NotificationUserEntity> listNotificationUser = notificationUserRepository
                .getListNotificationUserByUserIdOrderByCreatedTimeDesc(userId);

        Set<Long> setUserId = listNotificationUser
                .stream()
                .flatMap(n -> Stream.of(n.getUserCommentId(), n.getUserReceiveId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        List<UserEntity> listUser = userService.getListUserByListUserId(setUserId);
        Map<Long, String> mapUserNameByUserId = userService.getMapNameUserByUserId(listUser);
        Map<Long, String> mapUrlAvatarByUserId = userService.getMapUrlAvatarUserByUserId(listUser);

        List<Long> listCommentId = listNotificationUser
                .stream()
                .map(NotificationUserEntity::getCommentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Map<Long, String> mapTitlePostByCommentId = commentService.getMapTitlePostByCommentId(listCommentId);

        List<Long> listReplyCommentId = listNotificationUser
                .stream()
                .map(NotificationUserEntity::getReplyCommentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Map<Long, String> mapTitlePostByReplyCommentId = replyCommentService.getMapTitlePostByReplyCommentId(listReplyCommentId);

        listNotificationUser.forEach(notification -> {
            TypeNotificationUser type = notification.getType();
            Long commentId = notification.getCommentId();
            Long replyCommentId = notification.getReplyCommentId();

            if (type == TypeNotificationUser.CONFIRM_COMMENT) {
                String titlePost = null;
                if (replyCommentId != null) {
                    titlePost = mapTitlePostByReplyCommentId.get(notification.getReplyCommentId());
                } else if (commentId != null) {
                    titlePost = mapTitlePostByCommentId.get(notification.getCommentId());
                }
                notification.setTitlePost(titlePost);

            } else if (type == TypeNotificationUser.MENTION_COMMENT || type == TypeNotificationUser.REPLY_COMMENT) {
                notification.setNameUserComment(mapUserNameByUserId.get(notification.getUserCommentId()));
                notification.setUrlAvatarUserComment(mapUrlAvatarByUserId.get(notification.getUserCommentId()));
            }

            notification.setTimeNotification(TimeUtils.convertTimeToString(notification.getCreatedTime()));
        });

        return listNotificationUser;
    }

    public int getNumberNotificationHavenWatch(Long userId) {
        return (int) notificationUserRepository.getNumberNotificationHavenWatch(userId);
    }

    public void setAllNotificationToWatched(Long userId) {
        List<NotificationUserEntity> listNotificationUser = notificationUserRepository.getByUserId(userId);
        if (listNotificationUser.isEmpty()) {
            return;
        }
        listNotificationUser.forEach(notification -> notification.setWatched(true));
        notificationUserRepository.saveAll(listNotificationUser);
    }

    public ServerResponseDto detail(Long notificationId) {
        Optional<NotificationUserEntity> notificationUserOptional = notificationUserRepository.findById(notificationId);
        if (!notificationUserOptional.isPresent()) {
            return new ServerResponseDto(ResponseCase.ERROR);
        }

        NotificationUserEntity notificationUser = notificationUserOptional.get();
        notificationUser.setRead(true);
        notificationUserRepository.save(notificationUser);

        TypeNotificationUser typeNotificationUser = notificationUser.getType();
        if (typeNotificationUser == TypeNotificationUser.CONFIRM_COMMENT
                || typeNotificationUser == TypeNotificationUser.REPLY_COMMENT
                || typeNotificationUser == TypeNotificationUser.MENTION_COMMENT) {
            return handleClickDetailNotification(notificationUser);
        }
        return new ServerResponseDto(ResponseCase.SUCCESS);
    }

    private ServerResponseDto handleClickDetailNotification(NotificationUserEntity notificationUser) {
        Long postId = getPostId(notificationUser);
        Long fieldId = null;
        if (postId != null) {
            fieldId = postService.getFieldIdByPostId(postId);
        }
        Long commentId = notificationUser.getCommentId();
        Long replyCommentId = notificationUser.getReplyCommentId();

        return new ServerResponseDto(ResponseCase.SUCCESS, new DataForDetailPost(fieldId, postId, commentId, replyCommentId));
    }

    private Long getPostId(NotificationUserEntity notificationUser) {
        Long replyCommentId = notificationUser.getReplyCommentId();
        Long commentId = notificationUser.getCommentId();

        if (replyCommentId != null) {
            return postService.getPostIdByReplyCommentId(replyCommentId);
        } else {
            return postService.getPostIdByCommentId(commentId);
        }
    }

    @Transactional
    public void deleteNotificationWhenReplyCommentDenied(Long replyCommentId) {
        notificationUserRepository.deleteNotificationWhenReplyCommentDenied(replyCommentId);
    }
}
