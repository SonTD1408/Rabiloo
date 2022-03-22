package com.sontd.bookingpj.repository.car;


import com.sontd.bookingpj.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CarCompanyRepository extends JpaRepository<UserEntity, Long> {
    @Query("select u from UserEntity u where u.role=2")
    List<UserEntity> getCompany();
}
