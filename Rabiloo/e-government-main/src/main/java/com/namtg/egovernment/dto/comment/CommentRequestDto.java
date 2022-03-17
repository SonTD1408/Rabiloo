package com.namtg.egovernment.dto.comment;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class CommentRequestDto {
    private Long commentId;
    private Long postId;
    private String content;
    private MultipartFile image;

    private boolean isHaveImage;
}
