package com.example.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

@Getter
@Setter
@Document(indexName = "posts")
public class Post {
    String postType;
    Date postDate;
    String senderId;

    String name;
    String city;
    String description;
    String image;
    Date foundDate;
    String contactMethod;


    public Post(){}

    public Post(String name, String city, String postType, String description, String image, Date postDate, Date foundDate, String contactMethod, String senderId) {
        this.name = name;
        this.city = city;
        this.postType = postType;
        this.description = description;
        this.image = image;
        this.postDate = postDate;
        this.foundDate = foundDate;
        this.contactMethod = contactMethod;
        this.senderId = senderId;
    }
}
