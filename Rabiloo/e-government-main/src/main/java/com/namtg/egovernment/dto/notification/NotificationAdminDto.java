package com.namtg.egovernment.dto.notification;

import com.namtg.egovernment.enum_common.TypeNotificationAdmin;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NotificationAdminDto {

    private TypeNotificationAdmin type;
    private Long userImpactId;
    private Long postId;
    private Long commentId;
    private Long replyCommentId;
}
