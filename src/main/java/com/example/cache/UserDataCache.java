package com.example.cache;

import com.example.botapi.BotState;
import com.example.service.postcreating.PostCache;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class UserDataCache implements DataCache {
    private Map<Integer, BotState> usersBotStates = new HashMap<>();
    private Map<Integer, PostCache> usersLostPostCreating = new HashMap<>();
    private Map<Integer, PostCache> usersGodsendPostCreating = new HashMap<>();

    @Override
    public void setUsersCurrentBotState(int userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    @Override
    public BotState getUsersCurrentBotState(int userId) {
        BotState botState = usersBotStates.get(userId);
        if (botState == null) {
            botState = BotState.SHOW_MAIN_MENU;
        }

        return botState;
    }

    @Override
    public void setUsersLostPostCache(int userId, PostCache postCache) {
        usersLostPostCreating.put(userId,postCache);
    }

    @Override
    public PostCache getUsersLostPostCache(int userId) {
        PostCache postCache = usersLostPostCreating.get(userId);
        if (postCache == null) {
            postCache = new PostCache("Пропажа",String.valueOf(userId));
        }

        return postCache;
    }

    @Override
    public void setUsersGodsendPostCache(int userId, PostCache postCache) {
        usersGodsendPostCreating.put(userId,postCache);
    }

    @Override
    public PostCache getUsersGodsendPostCache(int userId) {
        PostCache postCache = usersGodsendPostCreating.get(userId);
        if (postCache == null) {
            postCache = new PostCache("Знахідка", String.valueOf(userId));
        }

        return postCache;
    }

}
