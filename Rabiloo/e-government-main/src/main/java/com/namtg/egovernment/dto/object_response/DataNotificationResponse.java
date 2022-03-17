package com.namtg.egovernment.dto.object_response;

import com.namtg.egovernment.entity.comment.CommentEntity;
import com.namtg.egovernment.entity.discussion_post.PostEntity;
import com.namtg.egovernment.entity.reply_comment.ReplyCommentEntity;
import com.namtg.egovernment.enum_common.TypeNotificationAdmin;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DataNotificationResponse {
    private TypeNotificationAdmin typeNotificationAdmin;

    private PostEntity post;
    private CommentEntity comment;
    private ReplyCommentEntity replyComment;
    private Long postId;

}
