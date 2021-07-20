package org.jboss.fuse.tnb.elasticsearch.validation;

import static org.junit.jupiter.api.Assertions.fail;

import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ElasticsearchValidation {
    private static final Logger LOG = LoggerFactory.getLogger(ElasticsearchValidation.class);

    private final RestHighLevelClient client;

    public ElasticsearchValidation(RestHighLevelClient client) {
        this.client = client;
    }

    public void createIndex(String index) {
        try {
            client.indices().create(new CreateIndexRequest(index), RequestOptions.DEFAULT);
        } catch (IOException e) {
            fail("Exception while creating Elastic search index: ", e);
        }
    }

    public void deleteIndex(String index) {
        try {
            if (indexExists(index)) {
                LOG.debug("Deleting index {}", index);
                client.indices().delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT);
            }
        } catch (IOException e) {
            LOG.error("Error while cleaning up index {}: {}", index, e.getMessage(), e);
        }
    }

    public SearchHits getData(String index) {
        try {
            SearchRequest searchRequest = new SearchRequest(index);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

            searchSourceBuilder.query(QueryBuilders.matchAllQuery());

            searchRequest.source(searchSourceBuilder);

            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

            return response.getHits();
        } catch (Exception e) {
            LOG.trace("Error while getting elasticsearch data: {}", e.getMessage());
            return SearchHits.empty();
        }
    }

    public IndexResponse insert(String index, Map<String, Object> content) {
        try {
            return client.index(new IndexRequest(index).source(content), RequestOptions.DEFAULT);
        } catch (IOException e) {
            fail("Unable to create record: ", e);
        }
        return null;
    }

    public List<String> getIndices() {
        GetIndexRequest request = new GetIndexRequest("*");
        GetIndexResponse response = null;
        try {
            response = client.indices().get(request, RequestOptions.DEFAULT);
        } catch (ElasticsearchStatusException ese) {
            if (ese.getMessage().contains("index_not_found_exception")) {
                return new ArrayList<>();
            } else {
                throw ese;
            }
        } catch (IOException e) {
            fail("Unable to list indices: ", e);
        }
        return Arrays.asList(response.getIndices());
    }

    public boolean indexExists(String index) {
        try {
            return client.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT);
        } catch (IOException e) {
            fail("Unable to check if index exists: ", e);
            return false;
        }
    }
}
