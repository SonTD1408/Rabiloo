package com.namtg.egovernment.dto.field;

import com.namtg.egovernment.entity.field.FieldEntity;
import com.namtg.egovernment.entity.field.SubFieldEntity;
import com.namtg.egovernment.entity.news.NewsEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class FieldAndListNews {
    private FieldEntity field;
    private List<SubFieldEntity> listSubField;
    private NewsEntity newsLatest;
    private NewsEntity newsLatestTop2;
    private List<NewsEntity> listNews;
}
