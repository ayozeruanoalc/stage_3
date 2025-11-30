package com.guanchedata.infrastructure.adapters.apiservices;

import com.guanchedata.infrastructure.ports.IndexStore;
import com.guanchedata.infrastructure.ports.Tokenizer;
import com.guanchedata.infrastructure.adapters.tokenizer.TextTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class SearchService {
    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    private final IndexStore indexStore;
    private final Tokenizer tokenizer;

    public SearchService(IndexStore indexStore) {
        this.indexStore = indexStore;
        this.tokenizer = new TextTokenizer();
    }

    public Set<String> search(String query) {
        log.info("Executing search for query: {}", query);

        Set<String> tokens = tokenizer.tokenize(query);
        Set<String> results = new HashSet<>();

        for (String token : tokens) {
            String normalizedToken = token.toLowerCase();
            Set<String> documentsForToken = indexStore.getDocuments(normalizedToken);
            
            if (results.isEmpty()) {
                results.addAll(documentsForToken);
            } else {
                results.retainAll(documentsForToken);
            }
        }

        log.info("Search completed. Found {} documents", results.size());
        return results;
    }
}
