package com.namtg.egovernment.dto.reply_comment;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class ReplyCommentDto {
    private Long replyCommentId;
    private Long commentId;
    private String content;
    private Long userTagId;
    private MultipartFile image;

    private boolean isHaveUserTagEditReplyComment;
    private boolean isHaveImage;
}
