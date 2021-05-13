package com.example.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@Document(indexName = "bookmarks")
public class Bookmark {

    private String chatID;

    private String postID;

    public Bookmark(String chatID, String postID) {
        this.chatID = chatID;
        this.postID = postID;
    }


}
