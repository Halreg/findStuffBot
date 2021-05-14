package com.example.service.dbrelatedservices;

import com.example.model.Post;
import com.example.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class PostQueries {

    @Autowired
    private static PostRepository postRepository;

    public static void createProductIndexBulk(final List<Post> posts) {
        postRepository.saveAll(posts);
    }

    public static void SavePost(final Post post) {
        log.info(post.toString());
        postRepository.save(post);
    }

}
