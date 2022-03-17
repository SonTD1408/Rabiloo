package com.namtg.egovernment.entity.reply_comment;

import com.namtg.egovernment.enum_common.StatusComment;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "reply_comment")
public class ReplyCommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // người thực hiện trả lời bình luận
    private Long commentId;
    private Long tagUserId; // người được tag

    private String content;
    private String nameImage;
    private String urlImage;
    private int numberHeart;

    @Enumerated(EnumType.STRING)
    private StatusComment status;
    private Long reasonDeniedId;
    private String reasonDeniedOther;

    private boolean isEdited;

    private Long userConfirmId;

    private boolean isDeleted;
    private Date createdTime;
    private Date updatedTime;

    @Transient
    private String nameUser;

    @Transient
    private String urlAvatarUser;

    @Transient
    private String nameUserTag;

    @Transient
    private String nameUserConfirm;

    @Transient
    private String timeReplyComment;

    @Transient
    private String reasonDeniedContent;

    @Transient
    private String timeDeleteReplyComment;

    @Transient
    private boolean isLikedByCurrentUser;
}
