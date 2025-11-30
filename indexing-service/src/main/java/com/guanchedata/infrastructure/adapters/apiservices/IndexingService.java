package com.guanchedata.infrastructure.adapters.apiservices;

import com.guanchedata.infrastructure.ports.BookStore;
import com.guanchedata.infrastructure.ports.IndexStore;
import com.guanchedata.infrastructure.ports.Tokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class IndexingService {
    private static final Logger log = LoggerFactory.getLogger(IndexingService.class);

    private final IndexStore indexStore;
    private final Tokenizer tokenizer;
    private final BookStore bookStore;

    public IndexingService(IndexStore indexStore, Tokenizer tokenizer, BookStore bookStore) {
        this.indexStore = indexStore;
        this.tokenizer = tokenizer;
        this.bookStore = bookStore;
    }

    public void indexDocument(String documentId) {
        log.info("Starting indexing for document: {}", documentId);

        try {
            String content = bookStore.getBookContent(documentId);
            Set<String> tokens = tokenizer.tokenize(content);

            int tokenCount = 0;
            for (String token : tokens) {
                String normalizedToken = token.toLowerCase();
                indexStore.addEntry(normalizedToken, documentId);
                tokenCount++;
            }

            log.info("Document {} indexed successfully. Total tokens: {}", documentId, tokenCount);

        } catch (Exception e) {
            log.error("Error indexing document {}: {}", documentId, e.getMessage(), e);
            throw new RuntimeException("Failed to index document: " + documentId, e);
        }
    }
}
