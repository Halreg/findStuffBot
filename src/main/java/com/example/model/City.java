package com.example.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Setter
@Getter
@Document(indexName = "cities")
public class City {
    @Id
    private String id;

    private String city;

    private String region;

    public City(String id, String city, String region) {
        this.id = id;
        this.city = city;
        this.region = region;
    }

}