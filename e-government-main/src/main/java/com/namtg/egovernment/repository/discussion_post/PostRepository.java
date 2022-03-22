package com.namtg.egovernment.repository.discussion_post;

import com.namtg.egovernment.entity.discussion_post.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;


public interface PostRepository extends JpaRepository<PostEntity, Long> {

    @Query(value = "select p from PostEntity p " +
            "where p.title like concat ('%', ?1, '%') and p.isDeleted = false")
    Page<PostEntity> getPage(String keyword, Pageable pageable);

    PostEntity findByIdAndIsDeletedFalse(Long id);

    @Query(value = "select p from PostEntity p " +
            "where p.title like concat ('%', ?1, '%') " +
            "and p.fieldId = ?2 " +
            "and p.isDeleted = false " +
            "and p.isApproved = true")
    List<PostEntity> getListForUser(String keyword, Long fieldId);

    @Query(value = "select p.* from discussion_post as p " +
            "where p.seo = ?1 and p.is_deleted = false limit 1", nativeQuery = true)
    PostEntity findBySeoAndIsDeletedFalse(String seo);

    @Query(value = "select p from PostEntity p " +
            "where p.isDeleted = false and p.createdTime = (select max(p.createdTime) from PostEntity p where p.isDeleted = false) ")
    PostEntity getPostLatest();

    List<PostEntity> findByIdInAndIsDeletedFalse(Collection<Long> listPostId);

    @Query(value = "select p.id from PostEntity p " +
            "inner join CommentEntity c on c.postId = p.id " +
            "inner join ReplyCommentEntity rc on rc.commentId = c.id " +
            "where rc.id = ?1 and p.isDeleted = false")
    Long getPostIdByReplyCommentId(Long replyCommentId);

    List<PostEntity> findByIsDeletedFalse();

    @Query(value = "select p.* from discussion_post as p " +
            "where p.is_deleted = false order by p.created_time desc limit 5", nativeQuery = true)
    List<PostEntity> getListTop5Post();

    @Query(value = "select count(p) from PostEntity p " +
            "where p.title = ?1 and p.id <> ?2 and p.isDeleted = false")
    int countPostExist(String title, Long postId);

    @Query(value = "select count(p) from PostEntity p " +
            "where p.title = ?1 and p.isDeleted = false")
    int countPostExist(String title);
}
