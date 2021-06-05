package com.example.botapi.handlers.callbackquery;

import com.example.botapi.BotState;
import com.example.cache.UserDataCache;
import com.example.service.postsearching.PostSearchCache;
import com.example.service.postsearching.PostSearchCase;
import com.example.service.postsearching.SearchPostsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class GetGodsendPostsCallbackHandler implements CallbackQueryHandler {
    private UserDataCache userDataCache;
    private SearchPostsService myPostsService;

    public GetGodsendPostsCallbackHandler(UserDataCache userDataCache, SearchPostsService myPostsService) {
        this.userDataCache = userDataCache;
        this.myPostsService = myPostsService;
    }

    @Override
    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery) {
        PostSearchCache postSearchCache = userDataCache.getSearchPostsCache(callbackQuery.getFrom().getId(), PostSearchCase.GODSEND);
        return myPostsService.handleCallbackQuery(callbackQuery,postSearchCache ,userDataCache);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SEARCH_GODSEND_POSTS;
    }

}
