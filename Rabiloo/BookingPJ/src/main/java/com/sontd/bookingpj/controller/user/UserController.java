package com.sontd.bookingpj.controller.user;

import com.sontd.bookingpj.entity.UserEntity;
import com.sontd.bookingpj.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = {"/", "/home"})
    public String home(){
        return "user/home";
    }

    @PostMapping("/login")
    public String login(@RequestParam("userName") String userName,
                        @RequestParam("password") String pass,
                        HttpSession session){
        UserEntity result = userService.checkLogin(userName, pass);
        if(result!=null){
            session.setAttribute("role", result.getRole());
            session.setAttribute("user", userName);
            session.setAttribute("name",result.getName());
            session.setAttribute("id",result.getId());
            if (result.getRole()==1) {
                return "redirect:admin/home";
            }else if (result.getRole()==2){
                if (result.getStatus()==1){
                    return "redirect:admin/home";
                }else return "redirect:home";
            }
            else{
                return "redirect:home";
            }
        }
        return "redirect:home";
    }
    @RequestMapping("logout")
    public String logout(HttpSession session){
        session.removeAttribute("user");
        session.removeAttribute("role");
        session.removeAttribute("name");
        session.removeAttribute("id");
        return "redirect:home";
    }

    @RequestMapping("agency/register")
    public String agencyRegister(){
        return "user/agencyRegister";
    }

    @PostMapping("agencyRegister")
    public String executeAgencyRegister(@RequestParam("company") String company,
                                        @RequestParam("email") String email,
                                        @RequestParam("phone") String phone ,
                                        @RequestParam("user") String user,
                                        @RequestParam("password") String pass,
                                        @RequestParam("address") String address){
        userService.saveAgencyRegister(company,email,phone,user,pass,address);
        return "redirect:home";
    }
}
