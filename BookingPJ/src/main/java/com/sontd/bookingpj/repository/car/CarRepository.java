package com.sontd.bookingpj.repository.car;

import com.sontd.bookingpj.entity.admin.CarEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<CarEntity, Long> {

}
