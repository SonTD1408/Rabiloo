package com.namtg.egovernment.dto.discussion_post;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PostDto {
    private Long id;
    private Long fieldId;
    private String title;
    private String content;
    private String target;
    private String conclude;
    private String closingDeadline;
    private String listRoleId;
    private boolean isApproved;

}
