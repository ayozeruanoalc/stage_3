package com.guanchedata.infrastructure.adapters.apiservices;

import io.javalin.http.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SearchController {
    private static final Logger log = Logger.getLogger(SearchController.class.getName());

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    public void search(Context ctx) {
        String query = ctx.queryParam("q");
        String author = ctx.queryParam("author");
        String language = ctx.queryParam("language");
        String yearStr = ctx.queryParam("year");

        Integer year = null;
        if (yearStr != null && !yearStr.trim().isEmpty()) {
            try {
                year = Integer.parseInt(yearStr);
            } catch (NumberFormatException e) {
                Map<String, Object> error = new HashMap<>();
                error.put("status", "error");
                error.put("message", "Invalid year format");
                ctx.status(400).json(error);
                return;
            }
        }

        log.info(String.format("Search: q='%s', author='%s', language='%s', year=%s",
                query, author, language, year));

        if (query == null || query.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", "Query parameter 'q' is required");
            ctx.status(400).json(error);
            return;
        }

        try {
            List<SearchService.SearchResult> results = searchService.search(query, author, language, year);

            Map<String, Object> filters = new HashMap<>();
            filters.put("author", author != null ? author : "");
            filters.put("language", language != null ? language : "");
            filters.put("year", year != null ? year : "");

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("query", query);
            response.put("filters", filters);
            response.put("count", results.size());
            response.put("results", results.stream()
                    .map(r -> {
                        Map<String, Object> m = new HashMap<>();
                        m.put("id", r.getId());
                        m.put("title", r.getTitle());
                        m.put("author", r.getAuthor());
                        m.put("language", r.getLanguage());
                        m.put("year", r.getYear());
                        return m;
                    })
                    .collect(Collectors.toList())
            );

            ctx.json(response);

        } catch (Exception e) {
            log.severe("Search failed: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("status", "error");
            error.put("message", e.getMessage());
            ctx.status(500).json(error);
        }
    }

    public void health(Context ctx) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("service", "search");
        ctx.json(response);
    }
}
