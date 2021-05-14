package com.example.appconfig;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.example.repository")
@ComponentScan(basePackages = { "com.example" })
public class ElasticsearhConfig {

    @Bean
    public RestHighLevelClient client() {
        String apiKeyId = "2tc4tfdqc4";
        String apiKeySecret = "5zmhwypba";
        String apiKeyAuth = Base64.getEncoder().encodeToString(
                (apiKeyId + ":" + apiKeySecret)
                        .getBytes(StandardCharsets.UTF_8));
        RestClientBuilder builder = RestClient.builder(
                new HttpHost("findstuffbotrepo-682483057.us-east-1.bonsaisearch.net", 443, "https"));
        Header[] defaultHeaders =
                new Header[]{new BasicHeader("Authorization",
                        "ApiKey " + apiKeyAuth)};
        builder.setDefaultHeaders(defaultHeaders);

        return new RestHighLevelClient(builder);
    }

    @Bean
    public ElasticsearchOperations elasticsearchTemplate() {
        return new ElasticsearchRestTemplate(client());
    }
}
