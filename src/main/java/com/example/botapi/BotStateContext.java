package com.example.botapi;

import com.example.botapi.handlers.InputMessageHandler;
import com.example.botapi.handlers.callbackquery.CallbackQueryHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class BotStateContext {
    private Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();
    private Map<BotState, CallbackQueryHandler> callbackQueryHandlers;

    public BotStateContext(List<InputMessageHandler> messageHandlers, List<CallbackQueryHandler> callbackQueryHandlers) {
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(), handler));
        callbackQueryHandlers.forEach(handler -> this.callbackQueryHandlers.put(handler.getHandlerName(), handler));
    }

    public SendMessage processInputMessage(BotState currentState, Message message) {
        InputMessageHandler currentMessageHandler = findMessageHandler(currentState);
        return currentMessageHandler.handle(message);
    }

    private InputMessageHandler findMessageHandler(BotState currentState) {
        return messageHandlers.get(currentState);
    }

    private CallbackQueryHandler findCallbackQueryHandler(BotState currentState) {
        return callbackQueryHandlers.get(currentState);
    }

    public SendMessage processCallbackQuery(BotState currentState, CallbackQuery usersQuery) {
        CallbackQueryHandler currentCallbackQueryHandler = findCallbackQueryHandler(currentState);
        return currentCallbackQueryHandler.handleCallbackQuery(usersQuery);
    }

}





