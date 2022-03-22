package com.namtg.egovernment.entity.discussion_post;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "discussion_post_role_comment")
public class PostRoleCommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long discussionPostId;
    private Long roleId;

    public PostRoleCommentEntity() {
    }

    public PostRoleCommentEntity(Long discussionPostId, Long roleId) {
        this.discussionPostId = discussionPostId;
        this.roleId = roleId;
    }
}
