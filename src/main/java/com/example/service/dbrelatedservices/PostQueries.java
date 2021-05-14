package com.example.service.dbrelatedservices;

import com.example.model.Post;
import com.example.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PostQueries {

    private PostRepository postRepository;

    private PostQueries(PostRepository postRepository){
        this.postRepository = postRepository;
    }

    public void createProductIndexBulk(final List<Post> posts) {
        postRepository.saveAll(posts);
    }

    public void SavePost(final Post post) {
        log.info(post.toString());
        log.info(postRepository.toString());
        postRepository.save(post);
    }

}
