package com.namtg.egovernment.repository.reason_denied_comment;

import com.namtg.egovernment.entity.reason_denied_comment.ReasonDeniedCommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReasonDeniedCommentRepository extends JpaRepository<ReasonDeniedCommentEntity, Long> {
    @Query(value = "select r.content from ReasonDeniedCommentEntity r " +
            "where r.id = ?1 and r.isDeleted = false")
    String getContentByIdAndIsDeletedFalse(Long reasonDeniedId);

    @Query(value = "select r from ReasonDeniedCommentEntity r " +
            "where r.content like concat ('%', ?1, '%') and r.isDeleted = false")
    Page<ReasonDeniedCommentEntity> getPage(String keyword, Pageable pageable);

    ReasonDeniedCommentEntity findByIdAndIsDeletedFalse(Long id);

    List<ReasonDeniedCommentEntity> findByIsDeletedFalse();

    List<ReasonDeniedCommentEntity> findByIdInAndIsDeletedFalse(List<Long> listReasonDeniedId);
}
