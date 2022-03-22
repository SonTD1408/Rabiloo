package com.namtg.egovernment.service.like;

import com.google.common.collect.Maps;
import com.namtg.egovernment.dto.response.ResponseCase;
import com.namtg.egovernment.dto.response.ServerResponseDto;
import com.namtg.egovernment.entity.like.LikeEntity;
import com.namtg.egovernment.repository.like.LikeRepository;
import com.namtg.egovernment.service.comment.CommentService;
import com.namtg.egovernment.service.reply_comment.ReplyCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LikeService {
    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ReplyCommentService replyCommentService;

    public Map<Long, Boolean> getMapIsLikedByCommentId(List<Long> listCommentId, Long currentUserId) {
        if (listCommentId.isEmpty() || currentUserId == null) {
            return Collections.emptyMap();
        }

        List<LikeEntity> listLikeEntity = likeRepository.findByCommentIdInAndUserId(listCommentId, currentUserId);
        return listLikeEntity
                .stream()
                .collect(Collectors.toMap(LikeEntity::getCommentId, LikeEntity::isLiked));
    }

    public Map<Long, Boolean> getMapIsLikedByReplyCommentId(List<Long> listReplyCommentId, Long currentUserId) {
        if (listReplyCommentId.isEmpty() || currentUserId == null) {
            return Collections.emptyMap();
        }

        List<LikeEntity> listLikeEntity = likeRepository.findByReplyCommentIdInAndUserId(listReplyCommentId, currentUserId);
        return listLikeEntity
                .stream()
                .collect(Collectors.toMap(LikeEntity::getReplyCommentId, LikeEntity::isLiked));
    }

    @Transactional
    public ServerResponseDto likeComment(Long commentId, Long userId) {
        LikeEntity likeEntity = likeRepository.findByCommentIdAndUserId(commentId, userId);
        boolean isPlusHeart;
        if (likeEntity != null) {
            isPlusHeart = !likeEntity.isLiked();
            likeEntity.setLiked(!likeEntity.isLiked());
        } else {
            isPlusHeart = true;
            likeEntity = new LikeEntity(userId, commentId, null);
        }
        likeRepository.save(likeEntity);

        commentService.plusHeart(commentId, isPlusHeart ? 1 : -1);
        return new ServerResponseDto(ResponseCase.SUCCESS);
    }

    @Transactional
    public ServerResponseDto likeReplyComment(Long replyCommentId, Long userId) {
        LikeEntity likeEntity = likeRepository.findByReplyCommentIdAndUserId(replyCommentId, userId);
        boolean isPlusHeart;
        if (likeEntity != null) {
            isPlusHeart = !likeEntity.isLiked();
            likeEntity.setLiked(!likeEntity.isLiked());
        } else {
            isPlusHeart = true;
            likeEntity = new LikeEntity(userId, null, replyCommentId);
        }
        likeRepository.save(likeEntity);

        replyCommentService.plusHeart(replyCommentId, isPlusHeart ? 1 : -1);
        return new ServerResponseDto(ResponseCase.SUCCESS);
    }


}
