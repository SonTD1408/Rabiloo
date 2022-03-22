package com.namtg.egovernment.repository.calendar_delete_comment;

import com.namtg.egovernment.entity.calendar_delete_comment.CalendarDeleteCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface CalendarDeleteCommentRepository extends JpaRepository<CalendarDeleteCommentEntity, Long> {
    @Query(value = "select c.* from calendar_delete_comment as c " +
            "where c.time_delete = DATE(?1)", nativeQuery = true)
    List<CalendarDeleteCommentEntity> getListCalendarDeleteToday(Date now);

    @Modifying
    @Query("delete from CalendarDeleteCommentEntity c " +
            "where c.id in ?1")
    void deleteByListId(List<Long> listCalendarId);

    @Modifying
    @Query("delete from CalendarDeleteCommentEntity c " +
            "where c.commentId = ?1")
    void deleteByCommentId(Long commentId);

    @Modifying
    @Query("delete from CalendarDeleteCommentEntity c " +
            "where c.replyCommentId = ?1")
    void deleteByReplyCommentId(Long replyCommentId);

    @Query(value = "select c from CalendarDeleteCommentEntity c " +
            "where c.commentId in ?1")
    List<CalendarDeleteCommentEntity> findByCommentIdIn(List<Long> listCommentId);

    @Query(value = "select c from CalendarDeleteCommentEntity c " +
            "where c.replyCommentId in ?1")
    List<CalendarDeleteCommentEntity> findByReplyCommentIdIn(List<Long> listReplyCommentId);
}
