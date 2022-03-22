package com.namtg.egovernment.entity.like;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "like_tbl")
public class LikeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long commentId;
    private Long replyCommentId;
    private boolean isLiked;

    public LikeEntity() {
    }

    public LikeEntity(Long userId, Long commentId, Long replyCommentId) {
        this.userId = userId;
        this.commentId = commentId;
        this.replyCommentId = replyCommentId;
        this.isLiked = true;
    }
}
