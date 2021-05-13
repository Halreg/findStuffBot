package com.example.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "cities", type = "doc")
public class City {
    @Id
    private String id;

    private String city;

    private String region;
}