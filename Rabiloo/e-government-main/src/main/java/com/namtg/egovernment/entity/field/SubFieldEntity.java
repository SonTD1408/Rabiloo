package com.namtg.egovernment.entity.field;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "sub_field")
public class SubFieldEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long fieldId;
    private String name;

    private boolean isDeleted;
    private Date createdTime;
    private Date updatedTime;

}
