package com.namtg.egovernment.dto.field;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class FieldDto {
    private Long id;
    private String name;
    private List<SubFieldDto> listSubField;
}
