package com.namtg.egovernment.repository.notification;

import com.namtg.egovernment.entity.notification.StatusNotificationAdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StatusNotificationAdminRepository extends JpaRepository<StatusNotificationAdminEntity, Long> {

    @Query(value = "select sn from StatusNotificationAdminEntity sn " +
            "where sn.adminId = ?1 and sn.isWatched = false ")
    List<StatusNotificationAdminEntity> getListByAdminIdAndHavenWatch(Long adminId);

    StatusNotificationAdminEntity findByNotificationIdAndAdminId(Long notificationId, Long adminId);

    @Query(value = "select sn from StatusNotificationAdminEntity sn " +
            "where sn.adminId = ?1")
    List<StatusNotificationAdminEntity> getListByAdminId(Long adminId);
}
