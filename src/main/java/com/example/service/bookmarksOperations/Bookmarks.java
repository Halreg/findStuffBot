package com.example.service.bookmarksOperations;

import com.example.model.Bookmark;
import com.example.repository.BookmarkQueries;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Bookmarks {

    BookmarkQueries bookmarkQueries;

    private Bookmarks(BookmarkQueries bookmarkQueries){
        this.bookmarkQueries = bookmarkQueries;
    }

    public void addBookmark(String userId,String postId){
        Bookmark bookmark = bookmarkQueries.getBookmark(userId);
        if(bookmark == null){
            List<String> ids = new ArrayList<>();
            ids.add(postId);
            bookmark = new Bookmark(userId,ids);
            bookmarkQueries.addBookmark(bookmark);
        } else {
            List<String> ids = bookmark.getPostIDs();
            if (ids.contains(postId)) return;
            ids.add(postId);
            bookmark.setPostIDs(ids);
            bookmarkQueries.addBookmark(bookmark);
        }
    }

    public void deleteBookmark(String userId,String postId){
        Bookmark bookmark = bookmarkQueries.getBookmark(userId);
        if(bookmark == null) return;
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
        if (bookmark == null) return false;
        List<String> ids = bookmark.getPostIDs();
        return ids.contains(postId);
    }

}
