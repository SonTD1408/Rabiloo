package com.namtg.egovernment.dto.discussion_post;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RequestCreatePostDto {
    private Long fieldId;
    private String title;
    private String content;
    private String target;
    private String closingDeadline;
    private boolean isCanCreatePost;
}
