package com.namtg.egovernment.dto.object_request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ConfirmCommentDto {
    private String status;
    private int numberDaysDelete;
    private Long reasonDeniedId;
    private String contentReasonDeniedOther;
}
