package com.example.service.postcreating;

import com.example.botapi.BotState;
import com.example.botapi.TelegramFacade;
import com.example.botapi.handlers.InputMessageHandler;
import com.example.botapi.handlers.menu.MainMenuHandler;
import com.example.cache.UserDataCache;
import com.example.model.City;
import com.example.model.PostType;
import com.example.service.MainMenuService;
import com.example.service.ReplyMessagesService;
import com.example.service.cityOperations.CityQueries;
import com.example.service.dbrelatedservices.PostQueries;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

@Service
public class PostBuilderService {

    PostQueries postQueries;
    ReplyMessagesService messagesService;
    CityQueries cityQueries;
    private Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    private PostBuilderService(PostQueries postQueries, ReplyMessagesService messagesService, CityQueries cityQueries){
        this.postQueries = postQueries;
        this.messagesService = messagesService;
        this.cityQueries = cityQueries;
    }

    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery, PostCache postCache, UserDataCache userDataCache){
        Message message = callbackQuery.getMessage();
        if(callbackQuery.getData().equals(messagesService.getReplyText("buttons.postCreating.back"))){
            PostCreatingStage currentStage = postCache.getCurrentStage();
            if( currentStage == PostCreatingStage.START_CREATING ){
                userDataCache.deletePostCache(message.getFrom().getId(),postCache);
                userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.SHOW_MAIN_MENU);
                InputMessageHandler messageHandler = messageHandlers.get(BotState.SHOW_MAIN_MENU);
                return messageHandler.handle(message);

            } else {
                postCache.previousStage();
                userDataCache.setUsersPostCache(message.getFrom().getId(),postCache);
                return getRepliedText(message,postCache,userDataCache);
            }

        } else {
            return new SendMessage(message.getChatId(),"");
        }

    }

    public SendMessage getRepliedText(Message message, PostCache postCache, UserDataCache userDataCache){
        Long chatId = message.getChatId();
        SendMessage result;



        switch (postCache.getCurrentStage()){
            case START_CREATING:
                result = new SendMessage(chatId, postCache.cashedPost.getPostType().equals(PostType.LOSS)
                        ? messagesService.getReplyText("reply.createLostPost.askCity") : messagesService.getReplyText("reply.createGodsendPost.askCity"));
                postCache.nextStage();
                userDataCache.setUsersPostCache(message.getFrom().getId(),postCache);
                result.setReplyMarkup(getBackButtonForPostCreating());
                break;
            case ASK_CITY:

                String cityName = message.getText();

                if(cityName.length() < 3) {
                    result = new SendMessage(chatId, messagesService.getReplyText("reply.createPost.cityMinLengthValidation"));
                    result.setReplyMarkup(getBackButtonForPostCreating());
                    break;
                }

                List<City> cities = cityQueries.searchCity(cityName);

                if(cities.size() == 0){
                    result = new SendMessage(chatId, messagesService.getReplyText("reply.createPost.cityNotFound"));
                } else if(cities.size() == 1) {
                    City city = cities.get(0);
                    String replyCityName = city.getCity() + " , " + city.getRegion();
                    result = new SendMessage(chatId, messagesService.getReplyText("reply.createPost.cityFound") + " " + replyCityName);
                    result.setReplyMarkup(getBackButtonForPostCreating());
                    postCache.cashedPost.setCity(city.getCity());
                    postCache.nextStage();
                    userDataCache.setUsersPostCache(message.getFrom().getId(),postCache);
                } else {
                    result = new SendMessage(chatId, messagesService.getReplyText("reply.createPost.fewCities"));
                    result.setReplyMarkup(getBackButtonForPostCreating());
                }
                break;
            case ASK_NAME:
                result = new SendMessage(chatId, "Ask Image");
                postCache.cashedPost.setName("Загубленый телефон");
                postCache.nextStage();
                userDataCache.setUsersPostCache(message.getFrom().getId(),postCache);

                result.setReplyMarkup(getBackButtonForPostCreating());
                break;
            case ASK_IMAGE:
                result = new SendMessage(chatId, "DESCRIPTION");
                postCache.cashedPost.setImage("iVBORw0KGgoAAAANSUhEUgAAAAUA" +
                        "AAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO" +
                        "9TXL0Y4OHwAAAABJRU5ErkJggg==");
                postCache.nextStage();
                userDataCache.setUsersPostCache(message.getFrom().getId(),postCache);

                result.setReplyMarkup(getBackButtonForPostCreating());
                break;
            case ASK_DESCRIPTION:
                result = new SendMessage(chatId, "ask found date");
                postCache.cashedPost.setDescription("телефон знайденый там-то, модель така-то");
                postCache.nextStage();
                userDataCache.setUsersPostCache(message.getFrom().getId(),postCache);

                result.setReplyMarkup(getBackButtonForPostCreating());
                break;
            case ASK_FOUND_DATE:
                result = new SendMessage(chatId, "Ask contact method");
                postCache.cashedPost.setFoundDate(new Date());
                postCache.nextStage();
                userDataCache.setUsersPostCache(message.getFrom().getId(),postCache);

                result.setReplyMarkup(getBackButtonForPostCreating());
                break;
            case ASK_CONTACT_METHOD:
                result = new SendMessage(chatId, "created");
                postCache.cashedPost.setContactMethod("0996637915");
                postQueries.SavePost(postCache.cashedPost);
                userDataCache.deletePostCache(message.getFrom().getId(),postCache);
                userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.SHOW_MAIN_MENU);
                break;
            default:
                result = new SendMessage(chatId, "error");
                break;
        }

        return result;
    }

    private InlineKeyboardMarkup getBackButtonForPostCreating(){
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton backButton = new InlineKeyboardButton().setText(messagesService.getReplyText("buttons.postCreating.back"));
        backButton.setCallbackData(messagesService.getReplyText("buttons.postCreating.back"));

        List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
        keyboardRow.add(backButton);

        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        keyboardRows.add(keyboardRow);
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }

}
