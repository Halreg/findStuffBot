package com.example.service.postsearching;

import com.example.botapi.FindStuffBot;
import com.example.cache.UserDataCache;
import com.example.model.Post;
import com.example.service.ReplyMessagesService;
import com.example.service.bookmarksOperations.Bookmarks;
import com.example.service.dbrelatedservices.PostQueries;
import com.example.service.postpresntation.PostFormatter;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

@Service
public class SearchBookmarksService {

    private final PostQueries postQueries;
    private final ReplyMessagesService messagesService;
    private final PostFormatter postFormatter;
    private final Bookmarks bookmarks;

    private SearchBookmarksService(PostQueries postQueries, ReplyMessagesService messagesService,PostFormatter postFormatter, Bookmarks bookmarks){
        this.postQueries = postQueries;
        this.messagesService = messagesService;
        this.postFormatter = postFormatter;
        this.bookmarks = bookmarks;
    }

    public SendMessage getRepliedText(Message message, PostSearchCache postSearchCache, UserDataCache userDataCache, int user_id){
        List<Post> posts = postQueries.getBookmarksPosts(user_id);
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
        SendMessage result = new SendMessage(message.getChatId(), messagesService.getReplyText("buttons.postSearching.postsQuantity", posts.size(), postSearchCache.getPageNumber()+1, postSearchCache.getPageQuantity(posts.size())));
        result.setReplyMarkup(navigationButtons);
        return result;
    }

    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery, PostSearchCache postSearchCache, UserDataCache userDataCache){
        String callBackData = callbackQuery.getData();

        return new SendMessage();
    }

}
