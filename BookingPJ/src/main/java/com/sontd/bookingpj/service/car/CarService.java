package com.sontd.bookingpj.service.car;

import com.sontd.bookingpj.dto.car.AddCarDto;
import com.sontd.bookingpj.entity.UserEntity;
import com.sontd.bookingpj.entity.admin.CarEntity;
import com.sontd.bookingpj.entity.admin.TypeCarEntity;
import com.sontd.bookingpj.repository.car.CarCompanyRepository;
import com.sontd.bookingpj.repository.car.CarRepository;
import com.sontd.bookingpj.repository.car.CarTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService {
    @Autowired
    public CarTypeRepository carTypeRepository;

    @Autowired
    public CarCompanyRepository carCompanyRepository;

    @Autowired
    public CarRepository carRepository;

    public List<TypeCarEntity> getType(){
        return carTypeRepository.getType();
    }
    public List<UserEntity> getCompany(){
        return carCompanyRepository.getCompany();
    }
    public void addCar(AddCarDto addCarDto){
        CarEntity car = new CarEntity();
        System.out.println(addCarDto.getCompanyId());
        System.out.println(addCarDto.getSeatBooked());
        car.setIdCompany(addCarDto.getCompanyId());
        car.setType(addCarDto.getTypeId());
        car.setCost(addCarDto.getCost());
        car.setEmpty_spot(addCarDto.getEmptySpot());
        car.setLicensePlate(addCarDto.getLicensePlate());
        car.setFrom(addCarDto.getFrom());
        car.setTo(addCarDto.getTo());
        car.setDate(addCarDto.getDate());
        carRepository.save(car);

        List seatBook = addCarDto.getSeatBooked();
        System.out.println(seatBook.size());
        for (int i=0;i<seatBook.size();i++){

        }
    }
}
