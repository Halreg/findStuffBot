package com.example.service.postcreating;

import com.example.model.Post;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class PostBuilderService {

    public SendMessage getRepliedText(final long chatId, PostCache postCache){

        return new SendMessage(chatId, "try1");
    }

}
