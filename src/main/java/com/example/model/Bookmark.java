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
    private String chatID;

    private List<String> postIDs;

    public Bookmark(String chatID, List<String> postIDs) {
        this.chatID = chatID;
        this.postIDs = postIDs;
    }


}
