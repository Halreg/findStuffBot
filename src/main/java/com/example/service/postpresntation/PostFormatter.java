package com.example.service.postpresntation;

import com.example.botapi.FindStuffBot;
import com.example.model.Post;
import com.example.model.PostType;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;

@Service
public class PostFormatter {

    private static SendPhoto getPostImageTemplate(Long chatId,Post post) throws IOException {
        File postImage = new File(post.getId());
        byte[] decodedBytes = Base64.getDecoder().decode(post.getImage());
        FileUtils.writeByteArrayToFile(postImage, decodedBytes);

        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(postImage);
        return sendPhoto;
    }

    private static SendMessage getPostMessageTemplate(Long chatId,Post post) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String postMessage = "Місто: " + post.getCity() +
                "\nІм'я: " + post.getName() +
                "\nОпис: " + post.getDescription() +
                "\nЗасіб звя'зку: " + post.getContactMethod() +
                "\nДата " + (post.getPostType() == PostType.LOSS ? "втрати: ": "знаходження: ") + simpleDateFormat.format(post.getFoundDate()) ;

        return new SendMessage(chatId,postMessage);
    }

    public static void SendPost(Long chatId ,Post post) throws IOException, TelegramApiException {
        SendPhoto sendPhoto = getPostImageTemplate(chatId,post);
        SendMessage sendMessage = getPostMessageTemplate(chatId,post);
        SendPost(sendMessage ,sendPhoto);
    }

    private static void SendPost(SendMessage sendMessage, SendPhoto sendPhoto) throws TelegramApiException {
        FindStuffBot.bot.execute( sendPhoto);
        FindStuffBot.bot.sendMessage( sendMessage);
    }


    public static void SendMyPost(Long chatId, Post post) throws IOException, TelegramApiException {
        SendPhoto sendPhoto = getPostImageTemplate(chatId,post);
        SendMessage sendMessage = getPostMessageTemplate(chatId,post);
        SendPost(sendMessage ,sendPhoto);
    }
}
