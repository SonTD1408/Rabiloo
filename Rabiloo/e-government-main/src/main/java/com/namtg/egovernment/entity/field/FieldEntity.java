package com.namtg.egovernment.entity.field;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "field")
public class FieldEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private boolean isDeleted;
    private Date createdTime;
    private Date updatedTime;

    @Transient
    private List<SubFieldEntity> listSubField;
}
