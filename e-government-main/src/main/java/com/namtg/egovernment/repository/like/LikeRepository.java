package com.namtg.egovernment.repository.like;

import com.namtg.egovernment.entity.like.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    @Query(value = "select l from LikeEntity l " +
            "where l.commentId in ?1 and l.userId = ?2")
    List<LikeEntity> findByCommentIdInAndUserId(List<Long> listCommentId, Long currentUserId);

    LikeEntity findByCommentIdAndUserId(Long commentId, Long userId);

    LikeEntity findByReplyCommentIdAndUserId(Long replyCommentId, Long userId);

    @Query(value = "select l from LikeEntity l " +
            "where l.replyCommentId in ?1 and l.userId = ?2")
    List<LikeEntity> findByReplyCommentIdInAndUserId(List<Long> listReplyCommentId, Long currentUserId);
}
