package com.namtg.egovernment.dto.news;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class NewsDto {
    private Long id;
    private Long subFieldId;
    private MultipartFile image;
    private String title;
    private String content;
}
