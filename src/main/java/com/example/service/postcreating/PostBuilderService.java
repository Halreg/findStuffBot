package com.example.service.postcreating;

import com.example.cache.UserDataCache;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.jws.soap.SOAPBinding;

public class PostBuilderService {

    public static SendMessage getRepliedText(Message message, PostCache postCache, UserDataCache userDataCache){
        Long chatId = message.getChatId();
        SendMessage result;
        switch (postCache.getCurrentStage()){
            case START_CREATING:
                result = new SendMessage(chatId, "Ask City");
                postCache.nextStage();
                break;
            case ASK_CITY:
                result = new SendMessage(chatId, "Ask Name");
                postCache.cashedPost.setCity("Сумы");
                postCache.nextStage();
                break;
            case ASK_NAME:
                result = new SendMessage(chatId, "Ask Image");
                postCache.cashedPost.setName("Загубленый телефон");
                postCache.nextStage();
                break;
            case ASK_IMAGE:
                result = new SendMessage(chatId, "DESCRIPTION");
                postCache.nextStage();
                break;
            case ASK_DESCRIPTION:
                result = new SendMessage(chatId, "ask found date");
                postCache.nextStage();
                break;
            case ASK_FOUND_DATE:
                result = new SendMessage(chatId, "Ask contact method");
                postCache.nextStage();
                break;
            case ASK_CONTACT_METHOD:
                result = new SendMessage(chatId, "created");
                break;
            default:
                result = new SendMessage(chatId, "error");
                break;
        }
        switch (postCache.cashedPost.getPostType()){
            case LOSS:
                userDataCache.setUsersLostPostCache(message.getFrom().getId(),postCache);
                break;
            case GODSEND:
                userDataCache.setUsersGodsendPostCache(message.getFrom().getId(),postCache);
                break;
        }

        return result;
    }

}
