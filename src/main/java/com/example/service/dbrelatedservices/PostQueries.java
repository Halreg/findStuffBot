package com.example.service.dbrelatedservices;

import com.example.model.Post;
import com.example.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
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

    private final PostRepository postRepository;
    private final ElasticsearchOperations elasticsearchTemplate;

    private PostQueries(PostRepository postRepository, ElasticsearchOperations elasticsearchTemplate){
        this.postRepository = postRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    public void createProductIndexBulk(final List<Post> posts) {
        postRepository.saveAll(posts);
    }

    public void SavePost(final Post post) {
        log.info(post.toString());
        log.info(postRepository.toString());
        postRepository.save(post);
    }

    public List<Post> getMyPosts(int user_id){
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

}
