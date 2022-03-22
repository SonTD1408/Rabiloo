package com.namtg.egovernment.entity.discussion_post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.namtg.egovernment.entity.comment.CommentEntity;
import com.namtg.egovernment.entity.user.RoleEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "discussion_post")
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long fieldId;
    private String title;
    private String content;
    private String target;
    private String conclude;

    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date closingDeadline;
    private int numberView;
    private String seo;
    private boolean isApproved;
    private boolean isCreatedFromUser;

    private boolean isDeleted;
    private Long createdByUserId;
    private Long updatedByUserId;
    private Long approvedByUserId;
    private Date createdTime;
    private Date updatedTime;
    private Date approvedTime;

    @Transient
    private List<RoleEntity> listRole;

    @Transient
    private String nameCreator;

    @Transient
    private String nameUpdater;

    @Transient
    private String nameRoleCreator;

    @Transient
    private String nameRoleApprover;

    @Transient
    private String nameApprover;

    @Transient
    private List<CommentEntity> listComment;

    @Transient
    private Long numberComment;

    @Transient
    private String closingDeadlineStr;

    @Transient
    private String concludeParse;

}
