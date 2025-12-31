package com.guanchedata.infrastructure.adapters.apiservices;

import com.guanchedata.infrastructure.ports.IndexStore;
import com.guanchedata.infrastructure.ports.MetadataStore;
import com.guanchedata.model.BookMetadata;

import java.util.*;
import java.util.logging.Logger;

public class SearchService {
    private static final Logger log = Logger.getLogger(SearchService.class.getName());

    private final IndexStore indexStore;
    private final MetadataStore metadataStore;

    public SearchService(IndexStore indexStore, MetadataStore metadataStore) {
        this.indexStore = indexStore;
        this.metadataStore = metadataStore;
    }

    public List<SearchResult> search(String query, String author, String language, Integer year) {
        long startTime = System.currentTimeMillis();

        Set<String> contentResults = searchByContent(query);
        Set<String> finalResults = filterByMetadata(contentResults, author, language, year);
        List<SearchResult> enrichedResults = enrichWithMetadata(finalResults);

        long duration = System.currentTimeMillis() - startTime;
        log.info(String.format("Search: query='%s', author='%s', language='%s', year=%s, results=%d, time=%dms",
                query, author, language, year, enrichedResults.size(), duration));

        return enrichedResults;
    }

    private Set<String> searchByContent(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptySet();
        }

        String[] terms = query.toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ")
                .trim()
                .split("\\s+");

        Set<String> results = new HashSet<>();

        for (String term : terms) {
            if (term.isEmpty()) continue;

            Set<String> documentsForTerm = indexStore.getDocuments(term);

            if (results.isEmpty()) {
                results.addAll(documentsForTerm);
            } else {
                results.retainAll(documentsForTerm);
            }
        }

        return results;
    }

    private Set<String> filterByMetadata(Set<String> documentIds, String author, String language, Integer year) {
        if ((author == null || author.trim().isEmpty()) &&
                (language == null || language.trim().isEmpty()) &&
                year == null) {
            return documentIds;
        }

        Set<String> filtered = new HashSet<>();

        for (String docId : documentIds) {
            BookMetadata metadata = metadataStore.getMetadata(docId);

            if (metadata == null) continue;

            boolean matches = true;

            if (author != null && !author.trim().isEmpty()) {
                matches = metadata.getAuthor() != null &&
                        metadata.getAuthor().toLowerCase().contains(author.toLowerCase());
            }

            if (matches && language != null && !language.trim().isEmpty()) {
                matches = metadata.getLanguage() != null &&
                        metadata.getLanguage().toLowerCase().contains(language.toLowerCase());
            }

            if (matches && year != null) {
                matches = metadata.getYear() == year;
            }

            if (matches) {
                filtered.add(docId);
            }
        }

        return filtered;
    }

    private List<SearchResult> enrichWithMetadata(Set<String> documentIds) {
        List<SearchResult> results = new ArrayList<>();

        for (String docId : documentIds) {
            BookMetadata metadata = metadataStore.getMetadata(docId);

            SearchResult result = new SearchResult(
                    docId,
                    metadata != null ? metadata.getTitle() : "Unknown",
                    metadata != null ? metadata.getAuthor() : "Unknown",
                    metadata != null ? metadata.getLanguage() : "Unknown",
                    metadata != null ? metadata.getYear() : 0
            );

            results.add(result);
        }

        return results;
    }

    public static class SearchResult {
        private final String id;
        private final String title;
        private final String author;
        private final String language;
        private final int year;

        public SearchResult(String id, String title, String author, String language, int year) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.language = language;
            this.year = year;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getAuthor() { return author; }
        public String getLanguage() { return language; }
        public int getYear() { return year; }
    }
}
