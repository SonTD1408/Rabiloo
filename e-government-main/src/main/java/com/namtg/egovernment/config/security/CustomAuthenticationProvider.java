package com.namtg.egovernment.config.security;

import com.namtg.egovernment.entity.user.RoleEntity;
import com.namtg.egovernment.entity.user.UserEntity;
import com.namtg.egovernment.service.user.UserService;
import com.namtg.egovernment.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();
        UserEntity userEntity = userService.findByEmailAndPassword(email, password);

        if (userEntity == null) {
            throw new BadCredentialsException("Email or password is incorrect!");
        }
        Set<RoleEntity> setRole = userEntity.getRoles();
        Collection<SimpleGrantedAuthority> authorities = new HashSet<>();

        boolean isGovernment = false, isProvince = false, isDistrict = false, isCommune = false;
        for (RoleEntity role: setRole) {
            String roleName = role.getName().toString();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            if (Constant.GOVERNMENT.equals(roleName)) {
                isGovernment = true;
            }
            if (Constant.PROVINCE.equals(roleName)) {
                isProvince = true;
            }
            if (Constant.DISTRICT.equals(roleName)) {
                isDistrict = true;
            }
            if (Constant.COMMUNE.equals(roleName)) {
                isCommune = true;
            }
        }
        if (isGovernment) {
            UserDetails userDetails = new CustomUserDetail(userEntity.getId(), true, true, true, true, authorities);
            return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
        } else if (isProvince) {
            UserDetails userDetails = new CustomUserDetail(userEntity.getId(), false, true, true, true, authorities);
            return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
        } else if (isDistrict) {
            UserDetails userDetails = new CustomUserDetail(userEntity.getId(), false, false, true, true, authorities);
            return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
        } else if (isCommune) {
            UserDetails userDetails = new CustomUserDetail(userEntity.getId(), false, false, false, true, authorities);
            return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
        } else {
            UserDetails userDetails = new CustomUserDetail(userEntity.getId(), false, false, false, false, authorities);
            return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
        }

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
