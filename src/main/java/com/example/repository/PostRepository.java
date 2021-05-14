package com.example.repository;

import com.example.model.Post;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends ElasticsearchRepository<Post, String> {


    //Page<Post> findByAuthorsName(String name, SpringDataWebProperties.Pageable pageable);

    //@Query("{\"bool\": {\"must\": [{\"match\": {\"authors.name\": \"?0\"}}]}}")
    //Page<Post> findByAuthorsNameUsingCustomQuery(String name, Pageable pageable);
}