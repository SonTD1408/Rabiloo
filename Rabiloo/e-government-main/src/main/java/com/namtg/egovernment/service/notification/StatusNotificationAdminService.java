package com.namtg.egovernment.service.notification;

import com.namtg.egovernment.entity.notification.StatusNotificationAdminEntity;
import com.namtg.egovernment.repository.notification.StatusNotificationAdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StatusNotificationAdminService {
    @Autowired
    private StatusNotificationAdminRepository repository;

    public List<StatusNotificationAdminEntity> getByAdminIdAndHavenWatch(Long adminId) {
        if (adminId == null) {
            return Collections.emptyList();
        }
        return repository.getListByAdminIdAndHavenWatch(adminId);
    }

    public void saveList(List<StatusNotificationAdminEntity> listStatusNotification) {
        repository.saveAll(listStatusNotification);
    }

    public void setNotificationToRead(Long notificationId, Long adminId) {
        StatusNotificationAdminEntity statusNotificationAdmin = repository.findByNotificationIdAndAdminId(notificationId, adminId);
        if (statusNotificationAdmin == null || statusNotificationAdmin.isRead()) {
            return;
        }
        statusNotificationAdmin.setRead(true);
        repository.save(statusNotificationAdmin);
    }

    public List<StatusNotificationAdminEntity> getListNotificationByAdminId(Long adminId) {
        if (adminId == null) {
            return Collections.emptyList();
        }
        return repository.getListByAdminId(adminId);
    }
}
