package com.namtg.egovernment.entity.actionable;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "actionable")
public class ActionableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private boolean isManagePost;
    private boolean isManageCommentPost;
    private boolean isManageConclusionPost;
    private boolean isManageField;
    private boolean isManageReasonDeniedComment;
    private boolean isManageNews;
}
