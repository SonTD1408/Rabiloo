package com.sontd.bookingpj.api.admin;

import com.sontd.bookingpj.dto.car.AddCarDto;
import com.sontd.bookingpj.entity.UserEntity;
import com.sontd.bookingpj.entity.admin.TypeCarEntity;
import com.sontd.bookingpj.service.car.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/carManagement")
public class CarManagementApi {
    @Autowired
    private CarService carService;

    @PostMapping("/getType")
    public List<TypeCarEntity> getType(){
        return carService.getType();
    }

    @PostMapping("/getCompany")
    public List<UserEntity> getCompany(){
        return carService.getCompany();
    }

    @PostMapping("/addCar")
    public String addCar(@RequestBody AddCarDto addCarDto){
        carService.addCar(addCarDto);
        return "success";
    }
}
