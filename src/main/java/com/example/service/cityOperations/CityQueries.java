package com.example.service.cityOperations;

import com.example.model.City;
import com.example.repository.CityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CityQueries {

    private CityRepository cityRepository;
    private ElasticsearchOperations elasticsearchTemplate;

    private CityQueries(CityRepository cityRepository, ElasticsearchOperations elasticsearchTemplate){
        this.cityRepository = cityRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    public List<City> searchCity(String name){
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(org.elasticsearch.index.query.QueryBuilders
                            .matchQuery("city", name)).build();
        SearchHits<City> sampleEntities =
                    elasticsearchTemplate.search(searchQuery,City.class, IndexCoordinates.of("cities"));
        List<SearchHit<City>> searchHits = sampleEntities.getSearchHits();
        List<City> result = new ArrayList<>();
        searchHits.forEach(citySearchHit -> result.add(citySearchHit.getContent()));

        return result;
    }

}
