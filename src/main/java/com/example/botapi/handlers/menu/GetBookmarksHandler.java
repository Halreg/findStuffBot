package com.example.botapi.handlers.menu;

import com.example.botapi.BotState;
import com.example.botapi.handlers.InputMessageHandler;
import com.example.cache.UserDataCache;
import com.example.service.postsearching.PostSearchCache;
import com.example.service.postsearching.PostSearchState;
import com.example.service.postsearching.SearchBookmarksService;
import com.example.service.postsearching.SearchMyPostsService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class GetBookmarksHandler implements InputMessageHandler {

    private final SearchBookmarksService bookmarksService;
    private final UserDataCache userDataCache;

    private GetBookmarksHandler(SearchBookmarksService bookmarksService,UserDataCache userDataCache){
        this.bookmarksService = bookmarksService;
        this.userDataCache = userDataCache;
    }

    @Override
    public SendMessage handle(Message message) {
        PostSearchCache postSearchCache = userDataCache.getSearchPostsCache(message.getFrom().getId(), PostSearchState.BOOKMARKS);
        return bookmarksService.getRepliedText(message,postSearchCache,userDataCache,message.getFrom().getId());
    }

    @Override
    public BotState getHandlerName() {
        return BotState.CHECK_BOOKMARKS;
    }
}
