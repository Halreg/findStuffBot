package com.example.repository;

import com.example.model.Post;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository
public interface PostRepository extends ElasticsearchRepository<Post, String> {

}