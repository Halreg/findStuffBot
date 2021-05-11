package com.example.controller;


import com.example.botapi.FindStuffBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;


@Slf4j
@RestController
public class WebHookController {
    private final FindStuffBot telegramBot;

    public WebHookController(FindStuffBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    //public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        public int onUpdateReceived(@RequestBody Update update) {
        log.info("WEBHOOKED");
        return 200;
        //return telegramBot.onWebhookUpdateReceived(update);
    }

}

