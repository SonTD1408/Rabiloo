package com.namtg.egovernment.entity.news;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "news")
public class NewsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long subFieldId;
    private String title;
    private String content;
    private String seo;
    private String nameImage;
    private String urlImage;

    private boolean isDeleted;
    private Long createdByUserId;
    private Long updatedByUserId;
    private Date createdTime;
    private Date updatedTime;

    @Transient
    private Long fieldId;

    @Transient
    private String fieldName;

    @Transient
    private String subFieldName;

    @Transient
    private String contentParse;

    @Transient
    private String createdTimeStr;
}
