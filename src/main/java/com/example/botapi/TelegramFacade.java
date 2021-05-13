package com.example.botapi;

import com.example.botapi.handlers.callbackquery.CallbackQueryFacade;
import com.example.cache.UserDataCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;


@Service
@Slf4j
public class TelegramFacade {
    private UserDataCache userDataCache;
    private BotStateContext botStateContext;
    private CallbackQueryFacade callbackQueryFacade;

    public TelegramFacade(UserDataCache userDataCache, BotStateContext botStateContext,
                          CallbackQueryFacade callbackQueryFacade) {
        this.userDataCache = userDataCache;
        this.botStateContext = botStateContext;
        this.callbackQueryFacade = callbackQueryFacade;
    }

    public SendMessage handleUpdate(Update update) {
        SendMessage replyMessage = null;

        if (update.hasCallbackQuery()) {
            log.info("New callbackQuery from User: {} with data: {}", update.getCallbackQuery().getFrom().getUserName(),
                    update.getCallbackQuery().getData());
            return callbackQueryFacade.processCallbackQuery(update.getCallbackQuery());
        }


        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User:{}, chatId: {},  with text: {}",
                    message.getFrom().getUserName(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }

        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        int userId = message.getFrom().getId();
        BotState botState;
        SendMessage replyMessage;

        switch (inputMsg) {
            case "Додати пропажу":
                botState = BotState.CREATE_LOSS_POST;
                break;
            case "Додати знахідку":
                botState = BotState.CREATE_GODSEND_POST;
                break;
            case "Переглянути об'яви знахідок":
                botState = BotState.SEARCH_GODSEND_POSTS;
                break;
            case "Переглянути об'яви пропаж":
                botState = BotState.SEARCH_LOSS_POSTS;
                break;
            case "Переглянути мої об'яви":
                botState = BotState.SEARCH_MY_POSTS;
                break;
            case "Обране":
                botState = BotState.CHECK_BOOKMARKS;
                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(userId);
                break;
        }

        userDataCache.setUsersCurrentBotState(userId, botState);

        replyMessage = botStateContext.processInputMessage(botState, message);

        return replyMessage;
    }


}
