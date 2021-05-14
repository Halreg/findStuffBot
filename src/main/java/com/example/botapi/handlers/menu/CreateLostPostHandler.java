package com.example.botapi.handlers.menu;

import com.example.botapi.BotState;
import com.example.botapi.handlers.InputMessageHandler;
import com.example.cache.UserDataCache;
import com.example.service.postcreating.PostBuilderService;
import com.example.service.postcreating.PostCache;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class CreateLostPostHandler implements InputMessageHandler {

    private UserDataCache userDataCache;
    private PostBuilderService postBuilderService;

    public CreateLostPostHandler(UserDataCache userDataCache, PostBuilderService postBuilderService) {
        this.userDataCache = userDataCache;
        this.postBuilderService = postBuilderService;
    }

    @Override
    public SendMessage handle(Message message) {
        PostCache postCache = userDataCache.getUsersGodsendPostCache(message.getFrom().getId());
        return postBuilderService.getRepliedText(message, postCache , userDataCache);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.CREATE_LOSS_POST;
    }
}
