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

    private String name;

    private String region;

    public City(String id, String name, String region) {
        this.id = id;
        this.name = name;
        this.region = region;
    }

}