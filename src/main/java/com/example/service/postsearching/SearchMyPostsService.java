package com.example.service.postsearching;

import com.example.botapi.FindStuffBot;
import com.example.cache.UserDataCache;
import com.example.model.Post;
import com.example.service.ReplyMessagesService;
import com.example.service.dbrelatedservices.PostQueries;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
public class  SearchMyPostsService{

    private final PostQueries postQueries;
    private final ReplyMessagesService messagesService;

    private SearchMyPostsService(PostQueries postQueries, ReplyMessagesService messagesService){
        this.postQueries = postQueries;
        this.messagesService = messagesService;
        }

    public SendMessage getRepliedText(Message message, PostSearchCache postSearchCache, UserDataCache userDataCache){
        List<Post> posts = postQueries.getMyPosts(message.getFrom().getId());
        if(posts.isEmpty()) return new SendMessage(message.getChatId(),messagesService.getReplyText("reply.myPosts.empty") );
        List<Post> filteredPosts = postSearchCache.getPostsPage(posts);
        if(filteredPosts.isEmpty()) new SendMessage(message.getChatId(),"");

        for (Post post : filteredPosts){
            SendMessage replyPost = new SendMessage();
            replyPost.setChatId(message.getChatId());
            replyPost.setText(post.getName());
            replyPost.setReplyMarkup(getPostsButton(post));
            FindStuffBot.bot.sendMessage(replyPost);
        }
        InlineKeyboardMarkup navigationButtons = postSearchCache.getNavigationButtons(posts.size());
        return new SendMessage(message.getChatId(), messagesService.getReplyText("buttons.postSearching.postsQuantity", posts.size(), postSearchCache.getPageNumber()+1, postSearchCache.getPageQuantity(posts.size()))).setReplyMarkup(navigationButtons);
    }

    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery, PostSearchCache postSearchCache, UserDataCache userDataCache){
        return new SendMessage();
    }

    private InlineKeyboardMarkup getPostsButton(Post post){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton getPost = new InlineKeyboardButton();
        getPost.setText(messagesService.getReplyText("buttons.postSearching.checkPost"));
        getPost.setCallbackData("getPost" + post.getId());

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(getPost);
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    }
