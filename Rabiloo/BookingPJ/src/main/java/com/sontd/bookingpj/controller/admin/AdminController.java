package com.sontd.bookingpj.controller.admin;

import com.sontd.bookingpj.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;


@Controller
public class AdminController {
    @Autowired
    public UserService userService;
    @RequestMapping("admin/home")
    public String adminHomePage(HttpSession session){
        int role = (int) session.getAttribute("role");
        if (role==3) {return "redirect:/home";}
        return "admin/home";
    }

    @RequestMapping("admin/agencyManagement")
    public String agencyManagementPage(HttpSession session){
        int role = (int) session.getAttribute("role");
        if(role==3){ return "redirect:/home";}
        return "admin/agencyManagement";}
}
