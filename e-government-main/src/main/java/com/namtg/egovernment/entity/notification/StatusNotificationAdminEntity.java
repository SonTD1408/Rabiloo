package com.namtg.egovernment.entity.notification;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "status_notification_admin")
public class StatusNotificationAdminEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long notificationId;
    private Long adminId;

    private boolean isRead;
    private boolean isWatched;
    private boolean isHidden;
}
