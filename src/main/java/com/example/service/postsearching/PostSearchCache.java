package com.example.service.postsearching;

import com.example.Main;
import com.example.model.Post;
import com.example.service.ReplyMessagesService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Setter
public class PostSearchCache {

    private int postsPerPage = 5;

    private int pageNumber = 0;
    private List<Post> postList;
    private PostSearchState postSearchState;


    public PostSearchCache(List<Post> postList,PostSearchState postSearchState){
        this.postList = postList;
        this.postSearchState = postSearchState;
    }

    public void nextPage(){
        pageNumber++;
    }

    public void previousPage(){
        if(pageNumber > 0) pageNumber--;
    }


    public List<Post> getPostsPage(List<Post> posts) {
        int lowerBound = pageNumber*postsPerPage;
        int upperBound = Math.min((pageNumber + 1) * postsPerPage, posts.size());
        if(upperBound<=lowerBound) return new ArrayList<>();
        return posts.subList(lowerBound, upperBound);
    }

    public InlineKeyboardMarkup getNavigationButtons(int postsQuantity){

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> keyboardRow = new ArrayList<>();

        if(pageNumber != 0) {
            InlineKeyboardButton left = new InlineKeyboardButton().setText("<");
            left.setCallbackData("<");
            keyboardRow.add(left);
        }
        if(pageNumber < (int)(Math.ceil(postsQuantity/postsPerPage)) - 1) {
            InlineKeyboardButton right = new InlineKeyboardButton().setText(">");
            right.setCallbackData(">");
            keyboardRow.add(right);
        }
        List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();
        keyboardRows.add(keyboardRow);
        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }

    public int getPageQuantity(int postsQuantity){
        Double result = Math.ceil(postsQuantity/postsPerPage);
        return result.intValue();
    }
}
