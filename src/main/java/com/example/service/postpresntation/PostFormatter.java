package com.example.service.postpresntation;

import com.example.botapi.FindStuffBot;
import com.example.model.Post;
import com.example.model.PostType;
import com.example.service.ReplyMessagesService;
import com.example.service.bookmarksOperations.Bookmarks;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class PostFormatter {

    @Autowired
    private ReplyMessagesService messagesService;
    @Autowired
    private Bookmarks bookmarks;

    private SendPhoto getPostImageTemplate(Long chatId,Post post) throws IOException {
        File postImage = new File(post.getId());
        byte[] decodedBytes = Base64.getDecoder().decode(post.getImage());
        FileUtils.writeByteArrayToFile(postImage, decodedBytes);

        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(postImage);
        return sendPhoto;
    }

    private SendMessage getPostMessageTemplate(Long chatId,Post post) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String postMessage = "Місто: " + post.getCity() +
                "\nІм'я: " + post.getName() +
                "\nОпис: " + post.getDescription() +
                "\nЗасіб звя'зку: " + post.getContactMethod() +
                "\nДата " + (post.getPostType() == PostType.LOSS ? "втрати: ": "знаходження: ") + simpleDateFormat.format(post.getFoundDate()) ;

        return new SendMessage(chatId,postMessage);
    }

    public void SendPost(Long chatId ,Post post) throws IOException, TelegramApiException {
        SendPhoto sendPhoto = getPostImageTemplate(chatId,post);
        SendMessage sendMessage = getPostMessageTemplate(chatId,post);
        SendPost(sendMessage ,sendPhoto);
    }

    private void SendPost(SendMessage sendMessage, SendPhoto sendPhoto) throws TelegramApiException {
        FindStuffBot.bot.execute( sendPhoto);
        FindStuffBot.bot.sendMessage( sendMessage);
    }


    public void SendMyPost(Long chatId, Post post, int userId) throws IOException, TelegramApiException {
        SendPhoto sendPhoto = getPostImageTemplate(chatId,post);
        SendMessage sendMessage = getPostMessageTemplate(chatId,post);

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton bookmark = new InlineKeyboardButton();
        if(bookmarks.isAddedToBookmarks(String.valueOf(userId), post.getId())){
            bookmark.setText(messagesService.getReplyText("buttons.postSearching.dellBookmark"));
            bookmark.setCallbackData("delPostB" + post.getId());
        } else {
            bookmark.setText(messagesService.getReplyText("buttons.postSearching.addBookmark"));
            bookmark.setCallbackData("addPostB" + post.getId());
        }
        InlineKeyboardButton delete = new InlineKeyboardButton();
        delete.setText(messagesService.getReplyText("buttons.postSearching.delete"));
        delete.setCallbackData("delPostD" + post.getId());

        InlineKeyboardButton back = new InlineKeyboardButton();
        back.setText(messagesService.getReplyText("buttons.postSearching.back"));
        back.setCallbackData("<<");

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(bookmark);
        row1.add(delete);
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(back);
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row1);
        keyboard.add(row2);
        inlineKeyboardMarkup.setKeyboard(keyboard);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        SendPost(sendMessage ,sendPhoto);
    }
}
