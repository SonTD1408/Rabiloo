package com.namtg.egovernment.service;

import com.namtg.egovernment.config.security.CustomUserDetail;
import org.springframework.stereotype.Service;

@Service
public class RedirectService {
    public String getUrlDefault(CustomUserDetail currentUserLogin) {
        boolean isAdmin = currentUserLogin.isGovernment() || currentUserLogin.isProvince()
                || currentUserLogin.isDistrict() || currentUserLogin.isCommune();
        if (isAdmin) {
            return "/admin/home";
        } else {
            return "/home";
        }
    }
}
