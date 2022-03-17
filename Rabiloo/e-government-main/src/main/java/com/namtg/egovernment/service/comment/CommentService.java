package com.namtg.egovernment.service.comment;

import com.google.common.collect.Maps;
import com.namtg.egovernment.dto.comment.CommentRequestDto;
import com.namtg.egovernment.dto.comment.PostIdAndNumberCommentObject;
import com.namtg.egovernment.dto.object_request.ConfirmCommentDto;
import com.namtg.egovernment.dto.response.ResponseCase;
import com.namtg.egovernment.dto.response.ServerResponseDto;
import com.namtg.egovernment.entity.comment.CommentEntity;
import com.namtg.egovernment.entity.notification.NotificationAdminEntity;
import com.namtg.egovernment.entity.reply_comment.ReplyCommentEntity;
import com.namtg.egovernment.entity.user.UserEntity;
import com.namtg.egovernment.enum_common.StatusComment;
import com.namtg.egovernment.enum_common.TypeNotificationAdmin;
import com.namtg.egovernment.repository.comment.CommentRepository;
import com.namtg.egovernment.service.AmazonService;
import com.namtg.egovernment.service.calendar_delete_comment.CalendarDeleteCommentService;
import com.namtg.egovernment.service.discussion_post.PostService;
import com.namtg.egovernment.service.like.LikeService;
import com.namtg.egovernment.service.notification.NotificationAdminService;
import com.namtg.egovernment.service.notification.NotificationUserService;
import com.namtg.egovernment.service.reason_denied_comment.ReasonDeniedCommentService;
import com.namtg.egovernment.service.reply_comment.ReplyCommentService;
import com.namtg.egovernment.service.user.UserService;
import com.namtg.egovernment.util.Constant;
import com.namtg.egovernment.util.DateUtils;
import com.namtg.egovernment.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ReplyCommentService replyCommentService;

    @Autowired
    private NotificationAdminService notificationAdminService;

    @Autowired
    private ReasonDeniedCommentService reasonDeniedCommentService;

    @Autowired
    private NotificationUserService notificationUserService;

    @Autowired
    private PostService postService;

    @Autowired
    private CalendarDeleteCommentService calendarDeleteCommentService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private AmazonService amazonService;

    @Transactional
    public ServerResponseDto submitComment(CommentRequestDto commentDto, Long userId) {
        if (userId == null) {
            return new ServerResponseDto(ResponseCase.ERROR);
        }

        Long commentId = commentDto.getCommentId();
        boolean isUpdate = commentId != null;
        CommentEntity comment;

        // isUpdate: true -> chỉnh sửa comment
        if (isUpdate) {
            comment = commentRepository.findByIdAndIsDeletedFalse(commentId);
            comment.setEdited(true);

            /* delete lịch xóa comment */
            calendarDeleteCommentService.deleteByCommentId(commentId);
        } else {
            comment = new CommentEntity();
            comment.setPostId(commentDto.getPostId());
            comment.setNumberHeart(0);
            comment.setCreatedTime(new Date());
        }
        comment.setUpdatedTime(new Date());
        comment.setUserId(userId);
        comment.setContent(commentDto.getContent());
        comment.setStatus(StatusComment.WAITING);

        MultipartFile image = commentDto.getImage();
        boolean isHaveImage = commentDto.isHaveImage();

        if (image != null) {
            String nameImage = image.getOriginalFilename();
            comment.setNameImage(nameImage);
            String urlImage = amazonService.uploadFile(image);
            comment.setUrlImage(urlImage);
        } else {
            if (comment.getNameImage() != null && !isHaveImage) {
                comment.setNameImage(null);
            }
        }
        comment = commentRepository.save(comment);

        /* create notification admin */
        NotificationAdminEntity notification = new NotificationAdminEntity();
        notification.setUserImpactId(userId);
        notification.setCommentId(comment.getId());
        notification.setCreatedTime(new Date());
        if (isUpdate) {
            notification.setType(TypeNotificationAdmin.EDIT_COMMENT);
        } else {
            notification.setType(TypeNotificationAdmin.COMMENT);
        }
        notificationAdminService.createNotificationSubmitComment(notification);
        /**/


        return new ServerResponseDto(ResponseCase.SUCCESS);
    }

    public List<CommentEntity> getListCommentByPostId(Long postId, Long currentUserId) {
        if (postId == null) {
            return Collections.emptyList();
        }
        List<CommentEntity> listComment = commentRepository.findByPostIdAndIsDeletedFalse(postId)
                .stream()
                .sorted(Comparator.comparing(CommentEntity::getUpdatedTime).reversed())
                .collect(Collectors.toList());
        if (listComment.isEmpty()) {
            return listComment;
        }

        List<Long> listUserId = listComment
                .stream()
                .map(CommentEntity::getUserId)
                .collect(Collectors.toList());
        List<UserEntity> listUser = userService.getListUserByListUserId(listUserId);
        Map<Long, String> mapNameUserCommentByUserId = userService.getMapNameUserByUserId(listUser);
        Map<Long, String> mapUrlAvatarUserByUserId = userService.getMapUrlAvatarUserByUserId(listUser);

        List<Long> listCommentId = listComment
                .stream()
                .map(CommentEntity::getId)
                .collect(Collectors.toList());
        Map<Long, List<ReplyCommentEntity>> mapListReplyCommentByCommentId = replyCommentService.getMapListReplyCommentByCommentId(listCommentId, currentUserId);
        Map<Long, Date> mapTimeDeleteCommentByCommentId = calendarDeleteCommentService.getMapTimeDeleteCommentByCommentId(listCommentId);
        Map<Long, Boolean> mapIsLikedByCommentId = likeService.getMapIsLikedByCommentId(listCommentId, currentUserId);

        List<Long> listReasonDeniedId = listComment
                .stream()
                .map(CommentEntity::getReasonDeniedId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        Map<Long, String> getMapContentReasonDeniedByCommentId = reasonDeniedCommentService.getMapContentReasonDeniedByReasonId(listReasonDeniedId);

        listComment.forEach(comment -> {
            comment.setTimeComment(TimeUtils.convertTimeToString(comment.getCreatedTime()));
            comment.setListReplyComment(mapListReplyCommentByCommentId.getOrDefault(comment.getId(), Collections.emptyList()));
            comment.setLikedByCurrentUser(mapIsLikedByCommentId.getOrDefault(comment.getId(), false));
            comment.setNameUserComment(mapNameUserCommentByUserId.get(comment.getUserId()));
            comment.setUrlAvatarUserComment(mapUrlAvatarUserByUserId.get(comment.getUserId()));

            if (comment.getStatus() == StatusComment.DENIED) {
                comment.setReasonDeniedContent(getMapContentReasonDeniedByCommentId.getOrDefault(comment.getReasonDeniedId(), comment.getReasonDeniedOther()));
                comment.setTimeDeleteComment(DateUtils.convertDateToString(mapTimeDeleteCommentByCommentId.get(comment.getId())));
            }
        });

        return listComment;
    }

    public Map<Long, Long> getMapNumberCommentByPostId(List<Long> listPostId) {
        if (listPostId.isEmpty()) {
            return Collections.emptyMap();
        }
        List<PostIdAndNumberCommentObject> listPostIdAndNumberComment = commentRepository.getListPostIdAndNumberComment(listPostId);

        Map<Long, Long> mapResult = new HashMap<>(listPostIdAndNumberComment.size());
        listPostIdAndNumberComment.forEach(object -> {
            mapResult.put(object.getPostId(), object.getNumberComment());
        });
        return mapResult;
    }

    public Long getPostIdByCommentId(Long commentId) {
        CommentEntity comment = commentRepository.getById(commentId);
        return comment != null ? comment.getPostId() : null;
    }

    public CommentEntity getById(Long commentId) {
        CommentEntity commentEntity = commentRepository.findByIdAndIsDeletedFalse(commentId);
        return commentEntity != null ? commentEntity : null;
    }

    @Transactional
    public ServerResponseDto confirmComment(Long commentId, ConfirmCommentDto confirmCommentDto, Long userConfirmId) {
        CommentEntity commentEntity = commentRepository.findByIdAndIsDeletedFalse(commentId);
        if (commentEntity == null) {
            return new ServerResponseDto(ResponseCase.ERROR);
        }

        String statusNotification = confirmCommentDto.getStatus();
        StatusComment statusComment = null;

        if (statusNotification.equals(Constant.APPROVED)) {
            statusComment = StatusComment.APPROVED;
        } else if (statusNotification.equals(Constant.DENIED)) {
            statusComment = StatusComment.DENIED;
            Long reasonDeniedId = confirmCommentDto.getReasonDeniedId();
            if (reasonDeniedId != 0) {
                commentEntity.setReasonDeniedId(reasonDeniedId);
            } else {
                String contentReasonDeniedOther = confirmCommentDto.getContentReasonDeniedOther();
                commentEntity.setReasonDeniedOther(contentReasonDeniedOther);
            }

            /* create lịch xóa comment */
            calendarDeleteCommentService.createCalendar(commentId, null, confirmCommentDto.getNumberDaysDelete());

        }
        commentEntity.setStatus(statusComment);
        commentEntity.setUserConfirmId(userConfirmId);

        commentRepository.save(commentEntity);

        /* create notification comment side user */
        Long userReceiveId = commentEntity.getUserId();
        notificationUserService.createNotificationConfirmComment(statusComment, userReceiveId, commentId, null);
        /*---------------------------------------*/

        return new ServerResponseDto(ResponseCase.SUCCESS);
    }

    public String getReasonDeniedContent(Long reasonDeniedId, String reasonDeniedOther) {
        if (reasonDeniedId == null) {
            return reasonDeniedOther;
        } else {
            return reasonDeniedCommentService.getContentReasonById(reasonDeniedId);
        }
    }

    public Map<Long, String> getMapTitlePostByCommentId(List<Long> listCommentId) {
        if (listCommentId.isEmpty()) {
            return Collections.emptyMap();
        }
        List<CommentEntity> listComment = commentRepository.findByIdIn(listCommentId);
        Set<Long> setPostId = listComment
                .stream()
                .map(CommentEntity::getPostId)
                .collect(Collectors.toSet());
        Map<Long, String> mapTitlePostByPostId = postService.getMapTitlePostByPostId(setPostId);

        Map<Long, String> mapResult = Maps.newHashMapWithExpectedSize(listCommentId.size());
        listComment.forEach(comment -> mapResult.put(comment.getId(), mapTitlePostByPostId.get(comment.getPostId())));

        return mapResult;
    }

    @Transactional
    public ServerResponseDto deleteComment(Long commentId) {
        CommentEntity commentEntity = commentRepository.findByIdAndIsDeletedFalse(commentId);
        if (commentEntity == null) {
            return new ServerResponseDto(ResponseCase.ERROR);
        }
        commentEntity.setDeleted(true);
        commentRepository.save(commentEntity);

        /* delete list reply_comment liên quan đến commentId này */
        replyCommentService.deleteListReplyCommentByCommentId(commentEntity.getId());

        /* delete lịch xóa comment */
        calendarDeleteCommentService.deleteByCommentId(commentId);

        return new ServerResponseDto(ResponseCase.SUCCESS);
    }

    public List<CommentEntity> getListCommentApprovedByPostId(Long postId) {
        List<CommentEntity> listCommentApproved = commentRepository.getListCommentApprovedByPostId(postId);

        List<Long> listUserId = listCommentApproved
                .stream()
                .map(CommentEntity::getUserId)
                .collect(Collectors.toList());
        Map<Long, String> mapNameUserCommentByUserId = userService.getMapNameUserByUserId(listUserId);

        List<Long> listCommentId = listCommentApproved
                .stream()
                .map(CommentEntity::getId)
                .collect(Collectors.toList());
        Map<Long, List<ReplyCommentEntity>> mapListReplyCommentByCommentId = replyCommentService.getMapListReplyCommentApprovedByCommentId(listCommentId);
        listCommentApproved.forEach(comment -> {
            comment.setTimeComment(TimeUtils.convertTimeToString(comment.getUpdatedTime()));
            comment.setListReplyComment(mapListReplyCommentByCommentId.getOrDefault(comment.getId(), Collections.emptyList()));
            comment.setNameUserComment(mapNameUserCommentByUserId.get(comment.getUserId()));
        });

        return listCommentApproved;
    }

    @Transactional
    public void deleteByListId(List<Long> listCommentIdDeleted) {
        if (listCommentIdDeleted.isEmpty()) {
            return;
        }
        commentRepository.deleteByListId(listCommentIdDeleted);
    }

    public void plusHeart(Long commentId, int numberHeart) {
        CommentEntity commentEntity = commentRepository.findByIdAndIsDeletedFalse(commentId);
        commentEntity.setNumberHeart(commentEntity.getNumberHeart() + numberHeart);
        commentRepository.save(commentEntity);
    }
}
