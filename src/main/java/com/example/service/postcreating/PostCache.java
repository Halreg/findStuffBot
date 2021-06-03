package com.example.service.postcreating;

import com.example.model.Post;
import com.example.model.PostType;

import java.util.*;

public class PostCache {

    public Post cashedPost;

    private static final List<PostCreatingStage> postCreatingCycle = Arrays.asList(
            PostCreatingStage.START_CREATING,
            PostCreatingStage.ASK_CITY,
            PostCreatingStage.ASK_NAME,
            PostCreatingStage.ASK_DESCRIPTION,
            PostCreatingStage.ASK_IMAGE,
            PostCreatingStage.ASK_FOUND_DATE,
            PostCreatingStage.ASK_CONTACT_METHOD,
            PostCreatingStage.CONFIRM_CREATION
            );

    private ListIterator<PostCreatingStage> currentStageIterator;

    public PostCache(PostType postType, String userID){
        cashedPost = new Post();
        cashedPost.setPostType(postType);
        cashedPost.setPostDate(new Date());
        cashedPost.setSenderId(userID);

        currentStageIterator = postCreatingCycle.listIterator();
    }

    public void nextStage(){
        if(currentStageIterator.hasNext()) currentStageIterator.next();
        else throw new IllegalStateException("Post Template Already at final Stage");
    }

    public void previousStage(){
        if(currentStageIterator.hasPrevious()) currentStageIterator.previous();
        else throw new IllegalStateException("Post Template Already at first Stage");
    }

    public PostCreatingStage getCurrentStage(){
        return postCreatingCycle.listIterator(currentStageIterator.nextIndex()).next();
    }


}
