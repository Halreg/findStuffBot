package com.example.service.postsearching;

import com.example.botapi.FindStuffBot;
import com.example.cache.UserDataCache;
import com.example.model.Post;
import com.example.service.ReplyMessagesService;
import com.example.service.bookmarksOperations.Bookmarks;
import com.example.service.dbrelatedservices.PostQueries;
import com.example.service.postpresntation.PostFormatter;
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
    private final PostFormatter postFormatter;
    private final Bookmarks bookmarks;

    private SearchPostsService(PostQueries postQueries, ReplyMessagesService messagesService, PostFormatter postFormatter, Bookmarks bookmarks){
        this.postQueries = postQueries;
        this.messagesService = messagesService;
        this.postFormatter = postFormatter;
        this.bookmarks = bookmarks;
        }

    public SendMessage getRepliedText(Message message, PostSearchCache postSearchCache, UserDataCache userDataCache, int user_id){
        List<Post> posts = postQueries.getPosts(user_id, postSearchCache.getPostSearchState());
        if(posts.isEmpty()) return new SendMessage(message.getChatId(),messagesService.getReplyText("reply.myPosts.empty") );
        List<Post> filteredPosts = postSearchCache.getPostsPage(posts);
        if(filteredPosts.isEmpty()) new SendMessage(message.getChatId(),"");

        FindStuffBot.bot.sendMessage(new SendMessage(message.getChatId(), "////////////////////"));
        for (Post post : filteredPosts){
            SendMessage replyPost = new SendMessage();
            replyPost.setChatId(message.getChatId());
            replyPost.setText(post.getName());
            replyPost.setReplyMarkup(postFormatter.getPostsButton(post));
            FindStuffBot.bot.sendMessage(replyPost);
        }
        InlineKeyboardMarkup navigationButtons = postSearchCache.getNavigationButtons(posts.size());
        log.info(navigationButtons.toString());
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
                postFormatter.sendFormatedPost(callbackQuery.getMessage().getChatId(),post, callbackQuery.getFrom().getId(), postSearchCache.getPostSearchState());
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
            return new SendMessage(callbackQuery.getMessage().getChatId(), "buttons.postCreating.deleted");
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
