package com.namtg.egovernment.controller;

import com.namtg.egovernment.config.security.CustomUserDetail;
import com.namtg.egovernment.entity.actionable.ActionableEntity;
import com.namtg.egovernment.entity.user.UserEntity;
import com.namtg.egovernment.service.actionable.ActionableService;
import com.namtg.egovernment.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.text.SimpleDateFormat;

@ControllerAdvice
public class UserInfoHeaderController {
    @Autowired
    private UserService userService;

    @Autowired
    private ActionableService actionableService;

    @ModelAttribute
    public void getCurrentUser(@AuthenticationPrincipal CustomUserDetail customUserDetail,
                               Model model) {

        if (customUserDetail == null) {
            return;
        }
        model.addAttribute("isGovernment", customUserDetail.isGovernment());
        model.addAttribute("isOnlyCitizen", customUserDetail.isOnlyCitizen());

        Long userId = customUserDetail.getId();
        UserEntity userEntity = userService.getById(userId);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (userEntity.getBirthday() != null) {
            userEntity.setBirthdayStr(sdf.format(userEntity.getBirthday()));
        } else {
            userEntity.setBirthdayStr("");
        }

        model.addAttribute("currentUser", userEntity);

        ActionableEntity actionableEntity = actionableService.findByUserId(userId);
        model.addAttribute("actionable", actionableEntity != null ? actionableEntity : new ActionableEntity());

    }
}
