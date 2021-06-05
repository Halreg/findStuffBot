package com.example.service.postsearching;

import com.example.botapi.FindStuffBot;
import com.example.cache.UserDataCache;
import com.example.model.City;
import com.example.model.Post;
import com.example.model.PostType;
import com.example.repository.CityQueries;
import com.example.service.ReplyMessagesService;
import com.example.service.bookmarksOperations.Bookmarks;
import com.example.repository.PostQueries;
import com.example.service.postpresntation.PostSender;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SearchPostsService {

    private final PostQueries postQueries;
    private final ReplyMessagesService messagesService;
    private final PostSender postSender;
    private final Bookmarks bookmarks;
    private final CityQueries cityQueries;

    private SearchPostsService(PostQueries postQueries, ReplyMessagesService messagesService, PostSender postSender, Bookmarks bookmarks, CityQueries cityQueries){
        this.postQueries = postQueries;
        this.messagesService = messagesService;
        this.postSender = postSender;
        this.bookmarks = bookmarks;
        this.cityQueries = cityQueries;
        }

    public SendMessage getRepliedText(Message message, PostSearchCache postSearchCache, UserDataCache userDataCache, int user_id){

        switch (postSearchCache.getCityAskStage()){
            case ASK_CITY:
                SendMessage result = new SendMessage(message.getChatId(), messagesService.getReplyText("reply.searchPosts.askCity"));
                postSearchCache.setCityAskStage(CityAskStage.CHECK_ANSWER);
                userDataCache.setSearchPostsCache(message.getFrom().getId(), postSearchCache);
                result.setReplyMarkup(postSender.getBackButtonForPostCreating());
                return result;
            case CHECK_ANSWER:
                String cityName = message.getText();
                Long chatId = message.getChatId();

                if(cityName == null || cityName.length() < 3) {
                    result = new SendMessage(chatId, messagesService.getReplyText("reply.createPost.cityMinLengthValidation"));
                    result.setReplyMarkup(postSender.getBackButtonForPostCreating());
                    break;
                }

                List<City> cities = cityQueries.searchCity(cityName);

                if(cities.size() == 0){
                    result = new SendMessage(chatId, messagesService.getReplyText("reply.createPost.cityNotFound"));
                } else if(cities.size() == 1) {
                    City city = cities.get(0);
                    String replyCityName = city.getCity() + " , " + city.getRegion();
                    result = new SendMessage(chatId, messagesService.getReplyText("reply.createPost.cityFound") + " " + replyCityName);
                    result.setReplyMarkup(postSender.getBackButtonForPostCreating());
                    FindStuffBot.bot.sendMessage(result);
                    postSearchCache.setCityName(city.getCity());
                    postSearchCache.setCityAskStage(CityAskStage.CITY_FOUND);
                    userDataCache.setSearchPostsCache(user_id,postSearchCache);
                    return getRepliedText(message,postSearchCache,userDataCache, user_id);
                } else {
                    result = new SendMessage(chatId, messagesService.getReplyText("reply.createPost.fewCities"));
                    result.setReplyMarkup(postSender.getBackButtonForPostCreating());
                }
                return result;
        }

        List<Post> posts = postQueries.getPosts(user_id, postSearchCache);

        if(posts ==null || posts.isEmpty()) {
            switch (postSearchCache.getPostSearchCase()) {
                case LOSS:
                case GODSEND:
                    return new SendMessage(message.getChatId(),messagesService.getReplyText("reply.Posts.empty"));
                case MY_POSTS:
                    return new SendMessage(message.getChatId(),messagesService.getReplyText("reply.myPosts.empty"));
                case BOOKMARKS:
                    return new SendMessage(message.getChatId(),messagesService.getReplyText("reply.bookmarks.empty"));
            }
        }
        List<Post> filteredPosts = postSearchCache.getPostsPage(posts);
        if(filteredPosts.isEmpty()) new SendMessage(message.getChatId(),"");

        FindStuffBot.bot.sendMessage(new SendMessage(message.getChatId(), "////////////////////"));
        for (Post post : filteredPosts){
            SendMessage replyPost = new SendMessage();
            replyPost.setChatId(message.getChatId());
            replyPost.setText("post.getName()");
            replyPost.setReplyMarkup(postSender.getPostsButton(post));
            FindStuffBot.bot.sendMessage(replyPost);
        }
        InlineKeyboardMarkup navigationButtons = postSearchCache.getNavigationButtons(posts.size());
        SendMessage result = new SendMessage(message.getChatId(), messagesService.getReplyText("buttons.postSearching.postsQuantity", posts.size(), postSearchCache.getPageNumber()+1, postSearchCache.getPageQuantity(posts.size())));
        result.setReplyMarkup(navigationButtons);
        return result;
    }

    public SendMessage
    handleCallbackQuery(CallbackQuery callbackQuery, PostSearchCache postSearchCache, UserDataCache userDataCache){
        String callBackData = callbackQuery.getData();

        if(callBackData.contains("getPostM")){
            Post post = postQueries.getPostById(callBackData.substring(8));
            if (post == null) return new SendMessage(callbackQuery.getMessage().getChatId(), messagesService.getReplyText("reply.getPost.missing"));
            try {
                postSender.sendFormatedPost(callbackQuery.getMessage().getChatId(),post, callbackQuery.getFrom().getId(), postSearchCache.getPostSearchCase());
                return new SendMessage();
            } catch (IOException | TelegramApiException e) {
                e.printStackTrace();
                return new SendMessage(callbackQuery.getMessage().getChatId(),messagesService.getReplyText("reply.getPost.error"));
            }
        }

        if(callBackData.contains("addPostB")){
            Post post = postQueries.getPostById(callBackData.substring(8));
            if (post == null) return new SendMessage(callbackQuery.getMessage().getChatId(), messagesService.getReplyText("reply.getPost.missing"));
            bookmarks.addBookmark(callbackQuery.getFrom().getId().toString(),post.getId());
        }

        if(callBackData.contains("delPostB")){
            Post post = postQueries.getPostById(callBackData.substring(8));
            if (post == null) return new SendMessage(callbackQuery.getMessage().getChatId(), messagesService.getReplyText("reply.getPost.missing"));
            bookmarks.deleteBookmark(callbackQuery.getFrom().getId().toString(),post.getId());
        }

        if(callBackData.contains("delPostD")){
            Post post = postQueries.getPostById(callBackData.substring(8));
            if (post == null) return new SendMessage(callbackQuery.getMessage().getChatId(), messagesService.getReplyText("reply.getPost.missing"));
            if(post.getSenderId().equals(callbackQuery.getFrom().getId().toString())){
                SendMessage result = new SendMessage(callbackQuery.getMessage().getChatId(),messagesService.getReplyText("buttons.postDelete.askConfirmation"));
                InlineKeyboardButton ask = new InlineKeyboardButton();
                ask.setText(messagesService.getReplyText("buttons.postCreating.confirm"));
                ask.setCallbackData("delPostC" + post.getId());
                List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
                keyboardRow.add(ask);
                List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                keyboard.add(keyboardRow);
                result.setReplyMarkup(new InlineKeyboardMarkup().setKeyboard(keyboard));
                return result;
            }
        }

        if(callBackData.contains("delPostC")){
            Post post = postQueries.getPostById(callBackData.substring(8));
            if (post == null) return new SendMessage(callbackQuery.getMessage().getChatId(), messagesService.getReplyText("reply.getPost.missing"));
            if(post.getSenderId().equals(callbackQuery.getFrom().getId().toString())){
                postQueries.deletePostById(post.getId());
            }
            return new SendMessage(callbackQuery.getMessage().getChatId(), messagesService.getReplyText("buttons.postCreating.deleted"));
        }

        switch (callBackData){
            case "<<":
                return getRepliedText(callbackQuery.getMessage(), postSearchCache, userDataCache,callbackQuery.getFrom().getId());
            case "<":
                postSearchCache.previousPage();
                userDataCache.setSearchPostsCache(callbackQuery.getFrom().getId(),postSearchCache);
                return getRepliedText(callbackQuery.getMessage(), postSearchCache, userDataCache,callbackQuery.getFrom().getId());
            case ">":
                postSearchCache.nextPage();
                userDataCache.setSearchPostsCache(callbackQuery.getFrom().getId(),postSearchCache);
                return getRepliedText(callbackQuery.getMessage(), postSearchCache, userDataCache,callbackQuery.getFrom().getId());
        }

        return new SendMessage();
    }

    }
