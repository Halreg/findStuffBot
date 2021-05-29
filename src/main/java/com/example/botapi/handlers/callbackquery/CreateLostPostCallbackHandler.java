package com.example.botapi.handlers.callbackquery;

import com.example.botapi.BotState;
import com.example.cache.UserDataCache;
import com.example.service.postcreating.PostBuilderService;
import com.example.service.postcreating.PostCache;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class CreateLostPostCallbackHandler implements CallbackQueryHandler{

    private UserDataCache userDataCache;
    private PostBuilderService postBuilderService;

    public CreateLostPostCallbackHandler(UserDataCache userDataCache, PostBuilderService postBuilderService) {
        this.userDataCache = userDataCache;
        this.postBuilderService = postBuilderService;
    }

    @Override
    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery) {
        PostCache postCache = userDataCache.getUsersLostPostCache(callbackQuery.getFrom().getId());
        return postBuilderService.handleCallbackQuery(callbackQuery, postCache, userDataCache);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.CREATE_LOSS_POST;
    }

}
