package com.example.service.postsearching;

import com.example.model.Post;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
    private PostSearchCase postSearchCase;
    CityAskStage cityAskStage = CityAskStage.CITY_FOUND;
    String cityName;


    public PostSearchCache(List<Post> postList, PostSearchCase postSearchCase){
        this.postList = postList;
        this.postSearchCase = postSearchCase;
        if(postSearchCase.equals(PostSearchCase.LOSS) || postSearchCase.equals(PostSearchCase.GODSEND) )cityAskStage = CityAskStage.ASK_CITY;
    }

    public void nextPage(){
        pageNumber++;
    }

    public void previousPage(){
        if(pageNumber > 0) pageNumber--;
    }


    public List<Post> getPostsPage(List<Post> posts) {
        if(pageNumber*postsPerPage > posts.size()){
            pageNumber = (int) Math.ceil((double) posts.size() / (double) postsPerPage) -1;
        }

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
        if(pageNumber < (int)(Math.ceil((double) postsQuantity/(double) postsPerPage)) - 1) {
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
        return (int) Math.ceil((double) postsQuantity / (double) postsPerPage);
    }

}
