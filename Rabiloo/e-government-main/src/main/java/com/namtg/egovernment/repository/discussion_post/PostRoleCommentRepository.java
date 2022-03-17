package com.namtg.egovernment.repository.discussion_post;

import com.namtg.egovernment.entity.discussion_post.PostRoleCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRoleCommentRepository extends JpaRepository<PostRoleCommentEntity, Long> {
    @Query(value = "select pr from PostRoleCommentEntity pr " +
            "where pr.discussionPostId = ?1")
    List<PostRoleCommentEntity> findByPostId(Long discussionPostId);

    @Modifying
    @Query(value = "delete from PostRoleCommentEntity pr where " +
            "pr.roleId in ?1 and pr.discussionPostId = ?2")
    void deleteByRoleIdInAndPostId(List<Long> listRoleIdDeleted, Long discussionPostId);

    @Query(value = "select pr from PostRoleCommentEntity pr " +
            "where pr.discussionPostId in ?1")
    List<PostRoleCommentEntity> findByPostIdIn(List<Long> listPostId);

    @Modifying
    @Query(value = "delete from PostRoleCommentEntity pr where " +
            "pr.discussionPostId = ?1")
    void deleteByPostId(Long postId);

}
