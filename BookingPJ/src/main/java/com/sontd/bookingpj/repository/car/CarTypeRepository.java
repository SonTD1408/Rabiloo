package com.sontd.bookingpj.repository.car;

import com.sontd.bookingpj.entity.admin.TypeCarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CarTypeRepository extends JpaRepository<TypeCarEntity, Long> {
    @Query("select tce from TypeCarEntity tce")
    List<TypeCarEntity> getType();
}
