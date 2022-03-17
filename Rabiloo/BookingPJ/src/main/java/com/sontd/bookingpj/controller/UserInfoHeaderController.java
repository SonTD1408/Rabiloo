package com.sontd.bookingpj.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpSession;

@ControllerAdvice
public class UserInfoHeaderController {
    @ModelAttribute
    public void currentUser(Model model, HttpSession session){
        if (session.getAttribute("name")!=null){
            model.addAttribute("name",session.getAttribute("name"));
        }else{
            model.addAttribute("name","null");
        }
    }
}
