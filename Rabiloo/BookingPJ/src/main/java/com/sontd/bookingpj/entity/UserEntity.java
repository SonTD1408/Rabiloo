package com.sontd.bookingpj.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name ="user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String companyName;
    private String userName;
    private String email;

    private String password;
    private String phone;
    private int gender;
    private String address;
    private int role;
    private int status;
}
