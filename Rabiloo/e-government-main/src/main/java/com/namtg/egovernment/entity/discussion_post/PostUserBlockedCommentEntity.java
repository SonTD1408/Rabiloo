package com.namtg.egovernment.entity.discussion_post;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "discussion_post_user_blocked_comment")
public class PostUserBlockedCommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long discussionPostId;
    private Long userId;
}
