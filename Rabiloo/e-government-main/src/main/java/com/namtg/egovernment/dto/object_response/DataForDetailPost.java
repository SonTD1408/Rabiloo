package com.namtg.egovernment.dto.object_response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DataForDetailPost {
    private Long fieldId;
    private Long postId;
    private Long commentId;
    private Long replyCommentId;

    public DataForDetailPost(Long fieldId, Long postId, Long commentId, Long replyCommentId) {
        this.fieldId = fieldId;
        this.postId = postId;
        this.commentId = commentId;
        this.replyCommentId = replyCommentId;
    }
}
