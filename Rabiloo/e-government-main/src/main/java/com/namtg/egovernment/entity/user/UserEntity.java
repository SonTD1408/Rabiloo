package com.namtg.egovernment.entity.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.namtg.egovernment.entity.actionable.ActionableEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameAvatar;
    private String urlAvatar;
    private String name;
    private String email;

    @JsonIgnore
    private String password;
    private String phone;
    @JsonFormat(pattern = "yyyy/MM/dd")
    private Date birthday;
    private int gender; // 1 male, 2 female, 9 other
    private String address;
    private int status;

    private boolean isDeleted;
    private Date createdTime;
    private Date updatedTime;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles;

    @Transient
        private String birthdayStr;

    @Transient
    private ActionableEntity actionableEntity;

    @Transient
    private String genderStr;

}
