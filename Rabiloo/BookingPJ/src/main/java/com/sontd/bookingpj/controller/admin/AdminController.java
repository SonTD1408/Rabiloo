package com.sontd.bookingpj.controller.admin;

import com.sontd.bookingpj.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;


@Controller
public class AdminController {
    @Autowired
    public UserService userService;
    @RequestMapping("admin/home")
    public String adminHomePage(HttpSession session, Model model){
        int role = (int) session.getAttribute("role");

        if (role==2 || role==1) {return "admin/home";}
        else return "redirect:/home";
    }

    @RequestMapping("admin/agencyManagement")
    public String agencyManagementPage(HttpSession session, Model model) {
        int role = (int) session.getAttribute("role");
        if (role == 2) return "redirect:/admin/home";
        else if (role == 1) return "admin/agencyManagement";
        return "redirect:/home";
    }

    @RequestMapping("admin/carManagement")
    public String carManagement(HttpSession session){
        int role = (int) session.getAttribute("role");
        if (role==2) return "/admin/carManagement";
        else return "/admin/home";
    }
}
