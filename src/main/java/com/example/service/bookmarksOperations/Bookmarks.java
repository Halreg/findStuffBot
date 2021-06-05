package com.example.service.bookmarksOperations;

import com.example.model.Bookmark;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Bookmarks {

    BookmarkQueries bookmarkQueries;

    private Bookmarks(BookmarkQueries bookmarkQueries){
        this.bookmarkQueries = bookmarkQueries;
    }

    public void addBookmark(String userId,String postId){
        Bookmark bookmark = bookmarkQueries.getBookmark(userId);
        List<String> ids = bookmark.getPostIDs();
        if(ids.contains(postId)) return;
        ids.add(postId);
        bookmark.setPostIDs(ids);
        bookmarkQueries.addBookmark(bookmark);
    }

    public void deleteBookmark(String userId,String postId){
        Bookmark bookmark = bookmarkQueries.getBookmark(userId);
        List<String> ids = bookmark.getPostIDs();
        if(!ids.contains(postId)) return;
        ids.remove(postId);
        bookmark.setPostIDs(ids);
        bookmarkQueries.addBookmark(bookmark);

    }

    public Bookmark getBookmark(String userId){
        return bookmarkQueries.getBookmark(userId);
    }

    public boolean isAddedToBookmarks(String userId,String postId){
        Bookmark bookmark = bookmarkQueries.getBookmark(userId);
        List<String> ids = bookmark.getPostIDs();
        return ids.contains(postId);
    }

}
