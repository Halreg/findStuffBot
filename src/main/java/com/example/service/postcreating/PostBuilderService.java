package com.example.service.postcreating;

import com.example.cache.UserDataCache;
import com.example.model.Post;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.jws.soap.SOAPBinding;
import java.util.Date;

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
                postCache.cashedPost.setImage("iVBORw0KGgoAAAANSUhEUgAAAAUA" +
                        "AAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO" +
                        "9TXL0Y4OHwAAAABJRU5ErkJggg==");
                postCache.nextStage();
                break;
            case ASK_DESCRIPTION:
                result = new SendMessage(chatId, "ask found date");
                postCache.cashedPost.setDescription("телефон знайденый там-то, модель така-то");
                postCache.nextStage();
                break;
            case ASK_FOUND_DATE:
                result = new SendMessage(chatId, "Ask contact method");
                postCache.cashedPost.setFoundDate(new Date());
                postCache.nextStage();
                break;
            case ASK_CONTACT_METHOD:
                result = new SendMessage(chatId, "created");
                postCache.cashedPost.setContactMethod("0996637915");
                savePostToDB(postCache.cashedPost);
                userDataCache.deletePostCache(message.getFrom().getId(),postCache);
                break;
            default:
                result = new SendMessage(chatId, "error");
                break;
        }

        userDataCache.setUsersPostCache(message.getFrom().getId(),postCache);

        return result;
    }

    static void savePostToDB(Post post){

    }

}
