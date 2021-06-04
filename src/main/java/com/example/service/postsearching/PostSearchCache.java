package com.example.service.postsearching;

import com.example.model.Post;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
public class PostSearchCache {

    @Value("${postPresentation.postsPerPage}")
    private int postsPerPage;
    private int pageNumber = 0;
    private List<Post> postList;
    PostSearchState postSearchState;

    public PostSearchCache(List<Post> postList,PostSearchState postSearchState){
        this.postList = postList;
        this.postSearchState = postSearchState;
    }

    public void nextPage(){
        pageNumber++;
    }

    public void previousPage(){
        if(pageNumber > 0) pageNumber--;
    }


    public List<Post> getPostsPage(List<Post> posts) {
        int lowerBound = pageNumber*postsPerPage;
        int upperBound = Math.min((pageNumber + 1) * postsPerPage, posts.size());
        log.warn(posts.size() + " " + lowerBound + " " + upperBound + " " + postsPerPage);
        if(upperBound<=lowerBound) return new ArrayList<>();
        return posts.subList(lowerBound, upperBound);
    }
}
