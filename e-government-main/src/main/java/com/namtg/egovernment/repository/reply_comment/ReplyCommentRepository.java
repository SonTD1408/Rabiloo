package com.namtg.egovernment.repository.reply_comment;

import com.namtg.egovernment.entity.reply_comment.ReplyCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReplyCommentRepository extends JpaRepository<ReplyCommentEntity, Long> {
    @Query(value = "select r from ReplyCommentEntity r " +
            "where r.commentId in ?1 and r.isDeleted = false")
    List<ReplyCommentEntity> getListReplyCommentByListCommentId(List<Long> listCommentId);

    ReplyCommentEntity findByIdAndIsDeletedFalse(Long replyCommentId);

    List<ReplyCommentEntity> findByIdIn(List<Long> listReplyCommentId);

    List<ReplyCommentEntity> findByCommentIdAndIsDeletedFalse(Long commentId);

    @Query(value = "select r from ReplyCommentEntity r " +
            "where r.commentId in ?1 and r.status = 'APPROVED' and r.isDeleted = false")
    List<ReplyCommentEntity> getListReplyCommentApprovedByListCommentId(List<Long> listCommentId);

    @Modifying
    @Query(value = "update ReplyCommentEntity r " +
            "set r.isDeleted = true " +
            "where r.id in ?1")
    void deleteByListId(List<Long> listReplyCommentIdDeleted);
}
