package com.namtg.egovernment.service.city;

import com.namtg.egovernment.entity.city.CityEntity;
import com.namtg.egovernment.repository.city.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {
    @Autowired
    private CityRepository cityRepository;

    public List<CityEntity> getList() {
        return cityRepository.findAll();
    }
}
