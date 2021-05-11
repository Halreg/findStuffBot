package com.example.botapi;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FindStuffBot extends TelegramWebhookBot {

    String botPath;
    String botUsername;
    String botToken;

    public FindStuffBot(DefaultBotOptions options) {
        super(options);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }


    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        log.info("New message from User:{}, chatId: {},  with text: {}",
                update.getMessage().getFrom().getUserName(), update.getMessage().getChatId(), update.getMessage().getText());
        return new SendMessage(update.getMessage().getFrom().getId().toString() , "Hi now");
    }

}
