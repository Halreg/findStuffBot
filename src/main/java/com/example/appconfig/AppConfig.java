package com.example.appconfig;


import com.example.botapi.FindStuffBot;
import com.example.botapi.TelegramFacade;
import com.example.botconfig.FindStuffBotConfig;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;


@Configuration
public class AppConfig {
    private FindStuffBotConfig botConfig;

    public AppConfig(FindStuffBotConfig findStuffBotConfig) {
        this.botConfig = findStuffBotConfig;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public FindStuffBot FindStuffTelegramBot(TelegramFacade telegramFacade) {
        DefaultBotOptions options = ApiContext
                .getInstance(DefaultBotOptions.class);

        FindStuffBot findStuffBot = new FindStuffBot(options,telegramFacade);
        findStuffBot.setBotUsername(botConfig.getUserName());
        findStuffBot.setBotToken(botConfig.getBotToken());
        findStuffBot.setBotPath(botConfig.getWebHookPath());

        return findStuffBot;
    }
}
