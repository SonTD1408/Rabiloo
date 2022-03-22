package com.namtg.egovernment.entity.notification;

import com.namtg.egovernment.enum_common.TypeNotificationAdmin;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "notification_admin")
public class NotificationAdminEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userImpactId;

    @Enumerated(EnumType.STRING)
    private TypeNotificationAdmin type;
    private Long commentId;
    private Long replyCommentId;
    private Long postId;

    private Date createdTime;

    /* tên user tác động đến thông báo */
    @Transient
    private String nameUserImpact;

    @Transient
    private String urlAvatarUserImpact;

    @Transient
    private String titlePost;

    @Transient
    private String timeNotification;

    @Transient
    private boolean isRead;

}
