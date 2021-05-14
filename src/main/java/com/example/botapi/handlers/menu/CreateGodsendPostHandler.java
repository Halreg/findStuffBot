package com.example.botapi.handlers.menu;

import com.example.botapi.BotState;
import com.example.botapi.handlers.InputMessageHandler;
import com.example.cache.DataCache;
import com.example.cache.UserDataCache;
import com.example.service.MainMenuService;
import com.example.service.ReplyMessagesService;
import com.example.service.postcreating.PostBuilderService;
import com.example.service.postcreating.PostCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class CreateGodsendPostHandler implements InputMessageHandler {

    private UserDataCache userDataCache;

    public CreateGodsendPostHandler(UserDataCache userDataCache) {
        this.userDataCache = userDataCache;
    }

    @Override
    public SendMessage handle(Message message) {
        PostCache postCache = userDataCache.getUsersGodsendPostCache(message.getFrom().getId());
        return PostBuilderService.getRepliedText(message, postCache , userDataCache);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.CREATE_GODSEND_POST;
    }

}
