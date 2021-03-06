package com.example.botapi;

import com.example.cache.UserDataCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;


@Service
@Slf4j
public class TelegramFacade {

    @Bean
    public UserDataCache getUserDataCache() {
        return userDataCache;
    }

    private final UserDataCache userDataCache;
    private final BotStateContext botStateContext;



    public TelegramFacade(UserDataCache userDataCache, BotStateContext botStateContext) {
        this.userDataCache = userDataCache;
        this.botStateContext = botStateContext;
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        SendMessage replyMessage = null;

        if (update.hasCallbackQuery()) {
            log.info("New callbackQuery from User: {} with data: {}", update.getCallbackQuery().getFrom().getUserName(),
                    update.getCallbackQuery().getData());
            BotState botState = userDataCache.getUsersCurrentBotState(update.getCallbackQuery().getFrom().getId());
            return botStateContext.processCallbackQuery( botState, update.getCallbackQuery());
        }


        Message message = update.getMessage();
        if (message != null) {
            log.info("New message from User:{}, chatId: {},  with text: {}",
                    message.getFrom().getUserName(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }

        return replyMessage;
    }

    public SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        int userId = message.getFrom().getId();
        BotState botState;
        SendMessage replyMessage;
        if (inputMsg!=null){
            switch (inputMsg) {
                case "???????????? ??????????????":
                    botState = BotState.CREATE_LOSS_POST;
                    break;
                case "???????????? ????????????????":
                    botState = BotState.CREATE_GODSEND_POST;
                    break;
                case "?????????????????????? ???????????????????? ????????????????":
                    botState = BotState.SEARCH_GODSEND_POSTS;
                    break;
                case "?????????????????????? ???????????????????? ????????????":
                    botState = BotState.SEARCH_LOSS_POSTS;
                    break;
                case "?????????????????????? ?????? ????????????????????":
                    botState = BotState.SEARCH_MY_POSTS;
                    break;
                case "????????????":
                    botState = BotState.CHECK_BOOKMARKS;
                    break;
                default:
                    botState = userDataCache.getUsersCurrentBotState(userId);
                    break;
            }
        } else {
            botState = userDataCache.getUsersCurrentBotState(userId);
        }

        userDataCache.setUsersCurrentBotState(userId, botState);
        replyMessage = botStateContext.processInputMessage(botState, message);
        return replyMessage;
    }


}
