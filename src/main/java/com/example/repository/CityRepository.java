package com.example.repository;

import com.example.model.City;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface CityRepository extends ElasticsearchRepository<City, String> {

    List<City> findByName(String name);

}