package com.example.service.postcreating;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class PostBuilderService {

    public static SendMessage getRepliedText(final long chatId, PostCache postCache){
        SendMessage result;
        switch (postCache.getCurrentStage()){
            case START_CREATING:
                result = new SendMessage(chatId, "Ask City");
                postCache.nextStage();
                break;
            case ASK_CITY:
                result = new SendMessage(chatId, "Ask Name");
                postCache.nextStage();
                break;
            case ASK_NAME:
                result = new SendMessage(chatId, "Ask Image");
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

        return result;
    }

}
