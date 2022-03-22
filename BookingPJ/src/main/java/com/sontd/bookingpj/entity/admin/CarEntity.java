package com.sontd.bookingpj.entity.admin;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name="car")
public class CarEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idCompany;
    private int type;
    private int empty_spot;
    private String licensePlate;
    private String from;
    private String to;
    private int cost;
    private Timestamp date;
}
