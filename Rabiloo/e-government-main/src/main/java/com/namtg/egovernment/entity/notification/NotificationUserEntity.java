package com.namtg.egovernment.entity.notification;

import com.namtg.egovernment.enum_common.StatusComment;
import com.namtg.egovernment.enum_common.TypeNotificationUser;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "notification_user")
public class NotificationUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userReceiveId;

    @Enumerated(EnumType.STRING)
    private TypeNotificationUser type;
    private Long commentId;
    private Long replyCommentId;
    private Long userCommentId; // người tác động đến comment hoặc reply_comment
    @Enumerated(EnumType.STRING)
    private StatusComment statusComment;

    private boolean isRead;
    private boolean isWatched;
    private boolean isHidden;

    private Date createdTime;

    @Transient
    private String titlePost;

    @Transient
    private String timeNotification;

    @Transient
    private String nameUserReceive;

    @Transient
    private String nameUserComment; // tên người tác động

    @Transient
    private String urlAvatarUserComment; // avatar người tác động
}
