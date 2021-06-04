package com.example.botapi.handlers.menu;

import com.example.botapi.BotState;
import com.example.botapi.handlers.InputMessageHandler;
import com.example.cache.UserDataCache;
import com.example.service.postsearching.PostSearchCache;
import com.example.service.postsearching.PostSearchState;
import com.example.service.postsearching.SearchMyPostsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class MyPostsHandler implements InputMessageHandler {

    private final SearchMyPostsService searchMyPostsService;
    private final UserDataCache userDataCache;

    private MyPostsHandler(SearchMyPostsService searchMyPostsService,UserDataCache userDataCache){
        this.searchMyPostsService = searchMyPostsService;
        this.userDataCache = userDataCache;
    }

    @Override
    public SendMessage handle(Message message) {
        PostSearchCache postSearchCache = userDataCache.getSearchPostsCache(message.getFrom().getId(), PostSearchState.MY_POSTS);
        return searchMyPostsService.getRepliedText(message,postSearchCache,userDataCache, message.getFrom().getId());
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SEARCH_MY_POSTS;
    }
}
