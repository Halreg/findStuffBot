package com.example.repository;

import com.example.model.City;
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
public class CityQueries {

    private ElasticsearchOperations elasticsearchTemplate;

    private CityQueries( ElasticsearchOperations elasticsearchTemplate){
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    public List<City> searchCity(String name){
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(org.elasticsearch.index.query.QueryBuilders
                            .matchPhrasePrefixQuery("city", name)).build();
        SearchHits<City> sampleEntities =
                    elasticsearchTemplate.search(searchQuery,City.class, IndexCoordinates.of("cities"));
        List<SearchHit<City>> searchHits = sampleEntities.getSearchHits();
        List<City> result = new ArrayList<>();
        searchHits.forEach(citySearchHit -> result.add(citySearchHit.getContent()));

        return result;
    }

}
