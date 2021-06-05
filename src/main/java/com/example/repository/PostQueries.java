package com.example.repository;

import com.example.model.Bookmark;
import com.example.model.Post;
import com.example.model.PostType;
import com.example.service.postsearching.PostSearchCache;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PostQueries {

    private final ElasticsearchOperations elasticsearchTemplate;

    private PostQueries( ElasticsearchOperations elasticsearchTemplate){
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    public void SavePost(final Post post) {
        elasticsearchTemplate.save(post,IndexCoordinates.of("posts"));
    }

    private List<Post> getMyPosts(int user_id){
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(org.elasticsearch.index.query.QueryBuilders
                        .termQuery("senderId", user_id)).build();
        SearchHits<Post> sampleEntities =
                elasticsearchTemplate.search(searchQuery,Post.class, IndexCoordinates.of("posts"));
        List<SearchHit<Post>> searchHits = sampleEntities.getSearchHits();
        List<Post> result = new ArrayList<>();
        searchHits.forEach(citySearchHit -> result.add(citySearchHit.getContent()));
        return result;
    }

    private List<Post> getPostsByCity(String city, PostType postType){
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.boolQuery().must( QueryBuilders.termQuery("city", city)).must(
                        QueryBuilders.termQuery("postType", postType.toString())
                )).build();
        SearchHits<Post> sampleEntities =
                elasticsearchTemplate.search(searchQuery,Post.class, IndexCoordinates.of("posts"));
        log.info(city);
        log.info(postType.toString());
        log.info(searchQuery.toString());
        log.info(sampleEntities.toString());
        List<SearchHit<Post>> searchHits = sampleEntities.getSearchHits();
        List<Post> result = new ArrayList<>();
        searchHits.forEach(citySearchHit -> result.add(citySearchHit.getContent()));
        return result;
    }

    public Post getPostById(String id){
        return elasticsearchTemplate.get(id,Post.class, IndexCoordinates.of("posts"));
    }

    public void deletePostById(String id) {
        elasticsearchTemplate.delete(id,IndexCoordinates.of("posts"));
    }

    private List<Post> getBookmarksPosts(int user_id) {
        Bookmark bookmark = elasticsearchTemplate.get(String.valueOf(user_id),Bookmark.class, IndexCoordinates.of("bookmarks"));
        if(bookmark == null) return null;
        List<Post> result = new ArrayList<>();
        for(String id : bookmark.getPostIDs()){
            result.add(elasticsearchTemplate.get(id,Post.class, IndexCoordinates.of("posts")));
        }
        return result;
    }

    public List<Post> getPosts(int user_id, PostSearchCache postSearchCache) {
        List<Post> result = null;
        switch (postSearchCache.getPostSearchCase()) {
            case LOSS:
                getPostsByCity(postSearchCache.getCityName(),PostType.LOSS);
                break;
            case GODSEND:
                getPostsByCity(postSearchCache.getCityName(),PostType.GODSEND);
                break;
            case MY_POSTS:
                result = getMyPosts(user_id);
                break;
            case BOOKMARKS:
                result = getBookmarksPosts(user_id);
                break;
        }
        return result;
    }
}
