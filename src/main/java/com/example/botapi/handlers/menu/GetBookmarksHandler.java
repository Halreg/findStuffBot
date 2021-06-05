package com.example.botapi.handlers.menu;

import com.example.botapi.BotState;
import com.example.botapi.handlers.InputMessageHandler;
import com.example.cache.UserDataCache;
import com.example.service.postsearching.PostSearchCache;
import com.example.service.postsearching.PostSearchCase;
import com.example.service.postsearching.SearchPostsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class GetBookmarksHandler implements InputMessageHandler {

    private final SearchPostsService searchPostsService;
    private final UserDataCache userDataCache;

    private GetBookmarksHandler(SearchPostsService searchPostsService,UserDataCache userDataCache){
        this.searchPostsService = searchPostsService;
        this.userDataCache = userDataCache;
    }

    @Override
    public SendMessage handle(Message message) {
        PostSearchCache postSearchCache = userDataCache.getSearchPostsCache(message.getFrom().getId(), PostSearchCase.BOOKMARKS);
        return searchPostsService.getRepliedText(message,postSearchCache,userDataCache,message.getFrom().getId());
    }

    @Override
    public BotState getHandlerName() {
        return BotState.CHECK_BOOKMARKS;
    }
}
