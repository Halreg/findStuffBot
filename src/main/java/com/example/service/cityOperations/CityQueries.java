package com.example.service.cityOperations;

import com.example.model.City;
import com.example.repository.CityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityQueries {

    private CityRepository cityRepository;

    private CityQueries(CityRepository cityRepository){
        this.cityRepository = cityRepository;
    }

    public List<City> searchCity(String name){
        return cityRepository.findByName(name);
    }

}
