package com.example.service.postcreating;

import com.example.botapi.BotState;
import com.example.botapi.FindStuffBot;
import com.example.botapi.handlers.InputMessageHandler;
import com.example.cache.UserDataCache;
import com.example.model.City;
import com.example.model.PostType;
import com.example.service.ReplyMessagesService;
import com.example.service.cityOperations.CityQueries;
import com.example.service.dbrelatedservices.PostQueries;
import com.example.service.postpresntation.PostFormatter;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class PostBuilderService {

    PostQueries postQueries;
    ReplyMessagesService messagesService;
    CityQueries cityQueries;
    PostFormatter postFormatter;

    private final Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    private PostBuilderService(PostQueries postQueries, ReplyMessagesService messagesService, CityQueries cityQueries,PostFormatter postFormatter){
        this.postQueries = postQueries;
        this.messagesService = messagesService;
        this.cityQueries = cityQueries;
        this.postFormatter = postFormatter;
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

        } else if(callbackQuery.getData().equals(messagesService.getReplyText("buttons.postCreating.confirm")) && postCache.getCurrentStage() == PostCreatingStage.CONFIRM_CREATION) {
            SendMessage result = new SendMessage(message.getChatId(), messagesService.getReplyText("reply.createPost.created"));
            postQueries.SavePost(postCache.cashedPost);
            userDataCache.deletePostCache(message.getFrom().getId(),postCache);
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.SHOW_MAIN_MENU);
            return result;
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

                if(cityName == null || cityName.length() < 3) {
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
                    result = new SendMessage(chatId, messagesService.getReplyText("reply.createPost.cityFound") + " " + replyCityName + "\n\n" +
                            messagesService.getReplyText("reply.createPost.askName"));
                    result.setReplyMarkup(getBackButtonForPostCreating());
                    postCache.cashedPost.setCity(city.getCity());
                    postCache.nextStage();
                    userDataCache.setUsersPostCache(message.getFrom().getId(),postCache);
                    result.setReplyMarkup(getBackButtonForPostCreating());
                } else {
                    result = new SendMessage(chatId, messagesService.getReplyText("reply.createPost.fewCities"));
                    result.setReplyMarkup(getBackButtonForPostCreating());
                }
                break;
            case ASK_NAME:
                String postName = message.getText();
                if(postName == null || postName.length() < 5) {
                    result = new SendMessage(chatId, messagesService.getReplyText("reply.createPost.postNameMinLengthValidation"));
                    result.setReplyMarkup(getBackButtonForPostCreating());
                    break;
                }

                result = new SendMessage(chatId, messagesService.getReplyText("reply.createPost.askDescription"));
                postCache.cashedPost.setName(postName);
                postCache.nextStage();
                userDataCache.setUsersPostCache(message.getFrom().getId(),postCache);

                result.setReplyMarkup(getBackButtonForPostCreating());
                break;
            case ASK_IMAGE:
                if(message.hasPhoto()){
                    List<PhotoSize> photos = message.getPhoto();
                    PhotoSize photo = photos.stream().max(Comparator.comparing(PhotoSize::getFileSize)).get();
                    File file;
                    try {
                        GetFile getFile = new GetFile().setFileId(photo.getFileId());
                        String filePath = FindStuffBot.bot.execute(getFile).getFilePath();
                        file = FindStuffBot.bot.downloadFile(filePath);
                    } catch (TelegramApiException e) {
                        result = new SendMessage(chatId, messagesService.getReplyText("reply.createPost.photoDownloadException"));
                        result.setReplyMarkup(getBackButtonForPostCreating());
                        e.printStackTrace();
                        break;
                    }

                    byte[] fileContent;
                    try {
                        fileContent = FileUtils.readFileToByteArray(file);
                    } catch (IOException e) {
                        result = new SendMessage(chatId, "File convertation Error");
                        result.setReplyMarkup(getBackButtonForPostCreating());
                        e.printStackTrace();
                        break;
                    }
                    String encodedString = Base64.getEncoder().encodeToString(fileContent);

                    postCache.cashedPost.setImage(encodedString);
                    result = new SendMessage(chatId, postCache.cashedPost.getPostType().equals(PostType.LOSS)
                            ? messagesService.getReplyText("reply.createLostPost.askFoundDate") : messagesService.getReplyText("reply.createGodsendPost.askFoundDate"));
                    postCache.nextStage();
                    userDataCache.setUsersPostCache(message.getFrom().getId(),postCache);
                } else {
                    result = new SendMessage(chatId, messagesService.getReplyText("reply.createPost.validatePhoto"));
                }
                result.setReplyMarkup(getBackButtonForPostCreating());

                break;
            case ASK_DESCRIPTION:
                String postDescription = message.getText();
                if( postDescription == null || postDescription.length() < 5) {
                    result = new SendMessage(chatId, messagesService.getReplyText("reply.createPost.descriptionMinLengthValidation"));
                    result.setReplyMarkup(getBackButtonForPostCreating());
                    break;
                }

                result = new SendMessage(chatId, messagesService.getReplyText("reply.createPost.askImage"));

                postCache.cashedPost.setDescription(postDescription);
                postCache.nextStage();
                userDataCache.setUsersPostCache(message.getFrom().getId(),postCache);

                InlineKeyboardMarkup replyKeyboardMarkup = getBackButtonForPostCreating();
                result.setReplyMarkup(replyKeyboardMarkup);
                break;
            case ASK_FOUND_DATE:
                result = new SendMessage(chatId, messagesService.getReplyText("reply.createPost.askContactMethod"));

                Date dateDepart;
                try {
                    dateDepart = new SimpleDateFormat("dd.MM.yyyy").parse(message.getText());
                } catch (ParseException e) {
                    return messagesService.getWarningReplyMessage(chatId, "reply.createPost.incorrectDateFormat");
                }

                postCache.cashedPost.setFoundDate(dateDepart);
                postCache.nextStage();
                userDataCache.setUsersPostCache(message.getFrom().getId(),postCache);

                result.setReplyMarkup(getBackButtonForPostCreating());
                break;
            case ASK_CONTACT_METHOD:
                String contactMethod = message.getText();

                if(contactMethod == null) {
                    result = new SendMessage(chatId, messagesService.getReplyText("reply.createPost.contactMethodNullValidation"));
                    result.setReplyMarkup(getBackButtonForPostCreating());
                    break;
                }

                postCache.cashedPost.setContactMethod(contactMethod);

                try {
                    postFormatter.sendPostWithoutButtons(chatId , postCache.cashedPost);
                } catch (IOException e) {
                    result = new SendMessage(chatId,"image convertation error");
                    e.printStackTrace();
                    break;
                } catch (TelegramApiException e) {
                    result = new SendMessage(chatId,"send image error");
                    e.printStackTrace();
                    break;
                }
                result = new SendMessage(chatId, messagesService.getReplyText("reply.createPost.askConfirmation"));
                postCache.nextStage();
                InlineKeyboardMarkup inlineKeyboardMarkup = getBackButtonForPostCreating();
                List<List<InlineKeyboardButton>> keyboard = inlineKeyboardMarkup.getKeyboard();
                keyboard.add(keyboard.get(0));

                InlineKeyboardButton backButton = new InlineKeyboardButton().setText(messagesService.getReplyText("buttons.postCreating.confirm"));
                backButton.setCallbackData(messagesService.getReplyText("buttons.postCreating.confirm"));
                List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
                keyboardRow.add(backButton);
                keyboard.set(0,keyboardRow);
                inlineKeyboardMarkup.setKeyboard(keyboard);

                userDataCache.setUsersPostCache(message.getFrom().getId(),postCache);
                result.setReplyMarkup(inlineKeyboardMarkup);
                break;
            case CONFIRM_CREATION:

                result = new SendMessage(chatId, "reply.createPost.reAskConfirmation");
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
