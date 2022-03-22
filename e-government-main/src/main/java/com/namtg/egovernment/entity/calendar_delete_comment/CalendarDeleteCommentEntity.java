package com.namtg.egovernment.entity.calendar_delete_comment;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "calendar_delete_comment")
public class CalendarDeleteCommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long commentId;
    private Long replyCommentId;
    private Date timeDelete;

    public CalendarDeleteCommentEntity() {
    }

    public CalendarDeleteCommentEntity(Long commentId, Long replyCommentId) {
        this.commentId = commentId;
        this.replyCommentId = replyCommentId;
    }
}
