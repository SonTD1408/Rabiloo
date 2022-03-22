package com.namtg.egovernment.repository.notification;

import com.namtg.egovernment.entity.notification.NotificationUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationUserRepository extends JpaRepository<NotificationUserEntity, Long> {

    @Query(value = "select n from NotificationUserEntity n " +
            "where n.userReceiveId = ?1 order by n.createdTime desc ")
    List<NotificationUserEntity> getListNotificationUserByUserIdOrderByCreatedTimeDesc(Long userReceiveId);

    @Query(value = "select count(n.id) from NotificationUserEntity n " +
            "where n.userReceiveId = ?1 and n.isWatched = false ")
    long getNumberNotificationHavenWatch(Long userId);

    @Query(value = "select n from NotificationUserEntity n " +
            "where n.userReceiveId = ?1")
    List<NotificationUserEntity> getByUserId(Long userId);

    @Modifying
    @Query(value = "delete from NotificationUserEntity n " +
            "where (n.type = 'REPLY_COMMENT' or n.type = 'MENTION_COMMENT') and n.replyCommentId = ?1")
    void deleteNotificationWhenReplyCommentDenied(Long replyCommentId);
}
