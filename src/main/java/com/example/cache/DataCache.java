package com.example.cache;


import com.example.botapi.BotState;
import com.example.service.postcreating.PostCache;

public interface DataCache {
    void setUsersCurrentBotState(int userId, BotState botState);

    BotState getUsersCurrentBotState(int userId);

    void setUsersLostPostCache(int userId, PostCache postCache);

    PostCache getUsersLostPostCache(int userId);

    void setUsersGodsendPostCache(int userId, PostCache postCache);

    PostCache getUsersGodsendPostCache(int userId);

}
