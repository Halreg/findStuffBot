package com.example.service.postcreating;

import com.example.model.Post;
import javafx.geometry.Pos;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class PostCache {

    Post cashedPost;

    public static final List<PostCreatingStage> postCreatingCycle = Arrays.asList(PostCreatingStage.ASK_CITY,
            PostCreatingStage.ASK_NAME,
            PostCreatingStage.ASK_DESCRIPTION,
            PostCreatingStage.ASK_IMAGE,
            PostCreatingStage.ASK_CONTACT_METHOD,
            PostCreatingStage.ASK_FOUND_DATE);

    public Iterator<PostCreatingStage> currentStage;

    public PostCache(String postType, String userID){
        cashedPost = new Post();
        cashedPost.setPostType(postType);
        cashedPost.setPostDate(new Date());
        cashedPost.setSenderId(userID);
    }


}
