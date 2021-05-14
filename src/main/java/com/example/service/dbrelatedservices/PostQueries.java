package com.example.service.dbrelatedservices;

import com.example.model.Post;
import com.example.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PostQueries {

    @Autowired
    private static PostRepository postRepository;

    public static void createProductIndexBulk(final List<Post> posts) {
        postRepository.saveAll(posts);
    }

    public static void SavePost(final Post post) {
        postRepository.save(post);
    }

}
