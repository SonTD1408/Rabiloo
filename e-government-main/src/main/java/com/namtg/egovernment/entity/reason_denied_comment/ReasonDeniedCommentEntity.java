package com.namtg.egovernment.entity.reason_denied_comment;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "reason_denied_comment")
public class ReasonDeniedCommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private boolean isDeleted;
    private Date createdTime;
    private Date updatedTime;
}
