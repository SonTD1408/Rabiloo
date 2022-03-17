package com.namtg.egovernment.repository.notification;

import com.namtg.egovernment.entity.notification.NotificationAdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationAdminRepository extends JpaRepository<NotificationAdminEntity, Long> {
    @Query(value = "select n from NotificationAdminEntity n " +
            "order by n.createdTime desc ")
    List<NotificationAdminEntity> getListNotification();

    @Query(value = "select count(n.id) from NotificationAdminEntity n " +
            "inner join StatusNotificationAdminEntity sn on n.id = sn.notificationId " +
            "where sn.adminId = ?1 and sn.isWatched = false")
    long getNumberNotificationHavenWatch(Long adminId);

    @Query(value = "select n from NotificationAdminEntity n " +
            "where n.id = ?1")
    NotificationAdminEntity getById(Long notificationId);

    @Query(value = "select n from NotificationAdminEntity n " +
            "where n.id in ?1 order by n.createdTime desc ")
    List<NotificationAdminEntity> getByListNotificationIdAndOrderByCreatedTimeDesc(List<Long> listNotificationId);
}
