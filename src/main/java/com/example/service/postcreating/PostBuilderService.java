package com.example.service.postcreating;

import com.example.botapi.BotState;
import com.example.botapi.TelegramFacade;
import com.example.botapi.handlers.InputMessageHandler;
import com.example.botapi.handlers.menu.MainMenuHandler;
import com.example.cache.UserDataCache;
import com.example.service.MainMenuService;
import com.example.service.ReplyMessagesService;
import com.example.service.dbrelatedservices.PostQueries;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;

@Service
public class PostBuilderService {

    PostQueries postQueries;
    ReplyMessagesService messagesService;
    private Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    private PostBuilderService(PostQueries postQueries,ReplyMessagesService messagesService){
        this.postQueries = postQueries;
        this.messagesService = messagesService;
    }

    public SendMessage getRepliedText(Message message, PostCache postCache, UserDataCache userDataCache){
        Long chatId = message.getChatId();
        SendMessage result;

        if(message.getText().equals(messagesService.getReplyText("buttons.postCreating.back"))){
            PostCreatingStage currentStage = postCache.getCurrentStage();
            if( currentStage == PostCreatingStage.START_CREATING){
                userDataCache.deletePostCache(message.getFrom().getId(),postCache);
                userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.SHOW_MAIN_MENU);
                InputMessageHandler messageHandler = messageHandlers.get(BotState.SHOW_MAIN_MENU);
                return messageHandler.handle(message);

            } else {
                postCache.previousStage();
                userDataCache.setUsersPostCache(message.getFrom().getId(),postCache);
            }

        }

        switch (postCache.getCurrentStage()){
            case START_CREATING:
                result = new SendMessage(chatId, "Ask City");
                postCache.nextStage();
                userDataCache.setUsersPostCache(message.getFrom().getId(),postCache);
                break;
            case ASK_CITY:
                result = new SendMessage(chatId, "Ask Name");
                postCache.cashedPost.setCity("Сумы");
                postCache.nextStage();
                userDataCache.setUsersPostCache(message.getFrom().getId(),postCache);
                break;
            case ASK_NAME:
                result = new SendMessage(chatId, "Ask Image");
                postCache.cashedPost.setName("Загубленый телефон");
                postCache.nextStage();
                userDataCache.setUsersPostCache(message.getFrom().getId(),postCache);
                break;
            case ASK_IMAGE:
                result = new SendMessage(chatId, "DESCRIPTION");
                postCache.cashedPost.setImage("iVBORw0KGgoAAAANSUhEUgAAAAUA" +
                        "AAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO" +
                        "9TXL0Y4OHwAAAABJRU5ErkJggg==");
                postCache.nextStage();
                userDataCache.setUsersPostCache(message.getFrom().getId(),postCache);
                break;
            case ASK_DESCRIPTION:
                result = new SendMessage(chatId, "ask found date");
                postCache.cashedPost.setDescription("телефон знайденый там-то, модель така-то");
                postCache.nextStage();
                userDataCache.setUsersPostCache(message.getFrom().getId(),postCache);
                break;
            case ASK_FOUND_DATE:
                result = new SendMessage(chatId, "Ask contact method");
                postCache.cashedPost.setFoundDate(new Date());
                postCache.nextStage();
                userDataCache.setUsersPostCache(message.getFrom().getId(),postCache);
                break;
            case ASK_CONTACT_METHOD:
                result = new SendMessage(chatId, "created");
                postCache.cashedPost.setContactMethod("0996637915");
                postQueries.SavePost(postCache.cashedPost);
                userDataCache.deletePostCache(message.getFrom().getId(),postCache);
                break;
            default:
                result = new SendMessage(chatId, "error");
                break;
        }
        result.enableMarkdown(true);
        result.setReplyMarkup(getBackButtonForPostCreating());
        return result;
    }

    private ReplyKeyboardMarkup getBackButtonForPostCreating(){
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setSelective(true);
        keyboardMarkup.setResizeKeyboard(true);
        keyboardMarkup.setOneTimeKeyboard(true);

        KeyboardButton backButton = new KeyboardButton().setText(messagesService.getReplyText("buttons.postCreating.back"));

        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(backButton);

        List<KeyboardRow> rowList = new ArrayList<>();

        keyboardMarkup.setKeyboard(rowList);
        return keyboardMarkup;
    }

}
