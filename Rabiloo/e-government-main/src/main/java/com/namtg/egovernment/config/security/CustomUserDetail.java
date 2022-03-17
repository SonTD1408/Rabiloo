package com.namtg.egovernment.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Setter
@Getter
public class CustomUserDetail extends User {
    private Long id;
    private boolean isGovernment;
    private boolean isProvince;
    private boolean isDistrict;
    private boolean isCommune;


    public CustomUserDetail(Long id, boolean isGovernment, boolean isProvince, boolean isDistrict, boolean isCommune, Collection<? extends GrantedAuthority> authorities) {
        super("username", "password", true, true, true, true, authorities);
        this.id = id;
        this.isGovernment = isGovernment;
        this.isProvince = isProvince;
        this.isDistrict = isDistrict;
        this.isCommune = isCommune;
    }

    public boolean isOnlyCitizen() {
        return !this.isGovernment() && !this.isProvince() && !this.isDistrict() && !this.isCommune();
    }
}
