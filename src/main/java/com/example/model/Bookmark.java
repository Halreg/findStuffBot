package com.example.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Getter
@Setter
@Document(indexName = "bookmarks")
public class Bookmark {

    @Id
    private String userId;

    private List<String> postIDs;

    public Bookmark(String userId, List<String> postIDs) {
        this.userId = userId;
        this.postIDs = postIDs;
    }


}
