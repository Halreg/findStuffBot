package com.example.botapi.handlers.menu;

import com.example.botapi.BotState;
import com.example.botapi.handlers.InputMessageHandler;
import com.example.cache.UserDataCache;
import com.example.service.postsearching.PostSearchCache;
import com.example.service.postsearching.PostSearchState;
import com.example.service.postsearching.SearchPostsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class SearchLostPostsHandler implements InputMessageHandler {

    private final SearchPostsService searchPostsService;
    private final UserDataCache userDataCache;

    private SearchLostPostsHandler(SearchPostsService searchPostsService,UserDataCache userDataCache){
        this.searchPostsService = searchPostsService;
        this.userDataCache = userDataCache;
    }

    @Override
    public SendMessage handle(Message message) {
        PostSearchCache postSearchCache = userDataCache.getSearchPostsCache(message.getFrom().getId(), PostSearchState.LOSS);
        return searchPostsService.getRepliedText(message,postSearchCache,userDataCache,message.getFrom().getId());
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SEARCH_LOSS_POSTS;
    }
}
