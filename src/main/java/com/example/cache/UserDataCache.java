package com.example.cache;

import com.example.botapi.BotState;
import com.example.model.PostType;
import com.example.service.dbrelatedservices.PostQueries;
import com.example.service.postcreating.PostCache;
import com.example.service.postsearching.PostSearchCache;
import com.example.service.postsearching.PostSearchState;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class UserDataCache{

    private final PostQueries postQueries;

    private UserDataCache(PostQueries postQueries){this.postQueries = postQueries;}

    private Map<Integer, BotState> usersBotStates = new HashMap<>();
    private Map<Integer, PostCache> usersLostPostCreating = new HashMap<>();
    private Map<Integer, PostCache> usersGodsendPostCreating = new HashMap<>();
    private Map<Integer, PostSearchCache> usersSearchPostCache = new HashMap<>();

    public void setUsersCurrentBotState(int userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    public BotState getUsersCurrentBotState(int userId) {
        BotState botState = usersBotStates.get(userId);
        if (botState == null) {
            botState = BotState.SHOW_MAIN_MENU;
        }

        return botState;
    }

    public PostCache getUsersLostPostCache(int userId) {
        PostCache postCache = usersLostPostCreating.get(userId);
        if (postCache == null) {
            postCache = new PostCache(PostType.LOSS,String.valueOf(userId));
        }

        return postCache;
    }

    public PostCache getUsersGodsendPostCache(int userId) {
        PostCache postCache = usersGodsendPostCreating.get(userId);
        if (postCache == null) {
            postCache = new PostCache(PostType.GODSEND, String.valueOf(userId));
        }

        return postCache;
    }

    public void setUsersPostCache(int userId, PostCache postCache) {
        switch (postCache.cashedPost.getPostType()){
            case LOSS:
                usersLostPostCreating.put(userId,postCache);
                break;
            case GODSEND:
                usersGodsendPostCreating.put(userId,postCache);
                break;
    }
    }

    public void deletePostCache(int userId, PostCache postCache) {
        switch (postCache.cashedPost.getPostType()){
            case LOSS:
                usersLostPostCreating.remove(userId);
                break;
            case GODSEND:
                usersGodsendPostCreating.remove(userId);
                break;
    }}


    public PostSearchCache getSearchPostsCache(int userId, PostSearchState postSearchState){
        PostSearchCache postSearchCache = usersSearchPostCache.get(userId);

        if (postSearchCache == null ||  (!postSearchState.equals(postSearchCache.getPostSearchState()))) {
            postSearchCache = new PostSearchCache(new ArrayList<>(), postSearchState);
        }

        return postSearchCache;
    }

    public void setSearchPostsCache(int userId, PostSearchCache postSearchCache){
        usersSearchPostCache.put(userId,postSearchCache);
    }

    public void deleteSearchPostsCache(int userId, PostSearchState postSearchState){
        if(usersSearchPostCache.get(userId).getPostSearchState() == postSearchState){
            usersSearchPostCache.remove(userId);
        }
    }
}
