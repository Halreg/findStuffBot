package com.example.repository;

import com.example.model.Bookmark;
import com.example.model.Post;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

@Service
public class BookmarkQueries {

    private final ElasticsearchOperations elasticsearchTemplate;

    private BookmarkQueries(ElasticsearchOperations elasticsearchTemplate){
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    public void addBookmark(Bookmark bookmark){
        elasticsearchTemplate.save(bookmark, IndexCoordinates.of("bookmarks"));
    }

    public Bookmark getBookmark(String id){
        return elasticsearchTemplate.get(id,Bookmark.class, IndexCoordinates.of("bookmarks"));
    }

}
