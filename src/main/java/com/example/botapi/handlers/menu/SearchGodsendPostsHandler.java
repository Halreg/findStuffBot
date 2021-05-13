package com.example.botapi.handlers.menu;

import com.example.botapi.BotState;
import com.example.botapi.handlers.InputMessageHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class SearchGodsendPostsHandler implements InputMessageHandler {

    @Override
    public SendMessage handle(Message message) {
        return new SendMessage(message.getChatId(), "Search Godsend");
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SEARCH_GODSEND_POSTS;
    }
}