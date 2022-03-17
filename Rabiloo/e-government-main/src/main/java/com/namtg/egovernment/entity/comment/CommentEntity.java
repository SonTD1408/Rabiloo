package com.namtg.egovernment.entity.comment;

import com.namtg.egovernment.entity.reply_comment.ReplyCommentEntity;
import com.namtg.egovernment.enum_common.StatusComment;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "comment")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // người thực hiện comment
    private Long postId;

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
    private String nameUserComment;

    @Transient
    private String urlAvatarUserComment;

    @Transient
    private String nameUserConfirm;

    @Transient
    private String timeComment;

    @Transient
    private List<ReplyCommentEntity> listReplyComment;

    @Transient
    private String reasonDeniedContent;

    @Transient
    private String timeDeleteComment;

    @Transient
    private boolean isLikedByCurrentUser;
}
