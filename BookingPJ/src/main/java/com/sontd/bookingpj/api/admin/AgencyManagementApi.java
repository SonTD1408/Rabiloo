package com.sontd.bookingpj.api.admin;

import com.sontd.bookingpj.entity.UserEntity;
import com.sontd.bookingpj.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/agencyManagement")
public class AgencyManagementApi {

    @Autowired
    private UserService userService;

    @GetMapping("/getAgency")
    public List<UserEntity> getAgency(){
        List<UserEntity> listAgency= userService.getAgency();
        return listAgency;
    }

    @PostMapping("/acceptAgency")
    public String acceptAgency(@RequestParam(value="id") long id){
        userService.acceptAgency(id);
//        if (agency!=null) return "success";
//        else return "fail";
        return "s";
    }

    @PostMapping("/saveAgencyDetail")
    public String saveAgencyDetail(@RequestBody UserEntity user){
        userService.saveAgencyDetail(user);
        return "";
    }

    @PostMapping("/deleteAgency")
    public String deleteAgency(@RequestParam(value = "id") long id){
        userService.deleteAgency(id);
        return "s";
    }
}
