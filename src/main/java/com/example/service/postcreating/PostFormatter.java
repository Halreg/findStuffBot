package com.example.service.postcreating;

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

    public static void SendPost(Long chatId ,Post post) throws IOException, TelegramApiException {
        File postImage = new File(post.getId());
        byte[] decodedBytes = Base64.getDecoder().decode(post.getImage());
        FileUtils.writeByteArrayToFile(postImage, decodedBytes);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String postMessage = "Місто: " + post.getCity() +
                "\nІм'я: " + post.getName() +
                "\nОпис: " + post.getDescription() +
                "\nЗасіб звя'зку:" + post.getContactMethod() +
                "\nДата " + (post.getPostType() == PostType.LOSS ? "втрати: ": "знаходження: ") + simpleDateFormat.format(post.getFoundDate()) ;
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(postImage);
        FindStuffBot.bot.execute(sendPhoto);
        FindStuffBot.bot.sendMessage( chatId, postMessage);
    }

}
