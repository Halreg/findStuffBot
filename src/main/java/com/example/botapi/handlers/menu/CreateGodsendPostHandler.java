package com.example.botapi.handlers.menu;

import com.example.botapi.BotState;
import com.example.botapi.handlers.InputMessageHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class CreateGodsendPostHandler implements InputMessageHandler {
    @Override
    public SendMessage handle(Message message) {
        return new SendMessage(message.getChatId(), "Godsend creating");
    }

    @Override
    public BotState getHandlerName() {
        return BotState.CREATE_GODSEND_POST;
    }
}
