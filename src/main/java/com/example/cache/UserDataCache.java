package com.example.cache;

import com.example.botapi.BotState;
import com.example.model.PostType;
import com.example.service.postcreating.PostCache;
import com.example.service.postsearching.PostSearchCache;
import com.example.service.postsearching.PostSearchState;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class UserDataCache{
    private Map<Integer, BotState> usersBotStates = new HashMap<>();
    private Map<Integer, PostCache> usersLostPostCreating = new HashMap<>();
    private Map<Integer, PostCache> usersGodsendPostCreating = new HashMap<>();
    private Map<Integer, PostSearchCache> usersSearchLostPost = new HashMap<>();
    private Map<Integer, PostSearchCache> usersSearchGodsendPost = new HashMap<>();
    private Map<Integer, PostSearchCache> myPosts = new HashMap<>();
    private Map<Integer, PostSearchCache> bookmarks = new HashMap<>();

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
        PostSearchCache postSearchCache = null;
        switch (postSearchState) {
            case LOSS:
                postSearchCache = usersSearchLostPost.get(userId);
                break;
            case GODSEND:
                postSearchCache = usersSearchGodsendPost.get(userId);
                break;
            case MY_POSTS:
                postSearchCache = myPosts.get(userId);
                break;
            case BOOKMARKS:
                postSearchCache = bookmarks.get(userId);
                break;
        }
        if (postSearchCache == null) {
            postSearchCache = new PostSearchCache();
        }

        return postSearchCache;
    }

    public void setSearchPostsCache(int userId, PostSearchCache postSearchCache, PostSearchState postSearchState){
        switch (postSearchState) {
            case LOSS:
                usersSearchLostPost.put(userId,postSearchCache);
                break;
            case GODSEND:
                usersSearchGodsendPost.put(userId,postSearchCache);
                break;
            case MY_POSTS:
                myPosts.put(userId,postSearchCache);
                break;
            case BOOKMARKS:
                bookmarks.put(userId,postSearchCache);
                break;
        }
    }

    public void deleteSearchPostsCache(int userId, PostSearchState postSearchState){
        switch (postSearchState) {
            case LOSS:
                usersSearchLostPost.remove(userId);
                break;
            case GODSEND:
                usersSearchGodsendPost.remove(userId);
                break;
            case MY_POSTS:
                myPosts.remove(userId);
                break;
            case BOOKMARKS:
                bookmarks.remove(userId);
                break;
        }
    }
}
