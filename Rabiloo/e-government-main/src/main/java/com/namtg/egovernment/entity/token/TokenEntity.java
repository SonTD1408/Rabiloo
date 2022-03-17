package com.namtg.egovernment.entity.token;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "token")
public class TokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String token;
    private Integer tokenType; // ACCESS_TOKEN(1), REFRESH_TOKEN(2),REGISTER_TOKEN(3), FORGOT_PASSWORD_TOKEN(4);
    private boolean isDeleted;
    private Date createdTime;

    public TokenEntity(Long userId, String token, Integer tokenType) {
        this.token = token;
        this.userId = userId;
        this.tokenType = tokenType;
        this.createdTime = new Date();
    }

    public TokenEntity() {
    }

    public TokenEntity(String token) {
        this.token = token;
    }

}
