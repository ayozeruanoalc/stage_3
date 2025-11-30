package com.guanchedata.infrastructure.adapters.bookstore;

import com.guanchedata.model.ReplicatedBook;
import com.guanchedata.infrastructure.ports.BookStore;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.multimap.MultiMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class HazelcastBookStore implements BookStore {
    private static final Logger log = LoggerFactory.getLogger(HazelcastBookStore.class);
    private final MultiMap<Integer, ReplicatedBook> datalakeMultiMap;

    public HazelcastBookStore(HazelcastInstance hazelcastInstance) {
        this.datalakeMultiMap = hazelcastInstance.getMultiMap("datalake");
        log.info("Connected to Hazelcast datalake MultiMap");
    }

    @Override
    public String getBookContent(String bookId) {
        try {
            Integer id = Integer.parseInt(bookId);
            Collection<ReplicatedBook> books = datalakeMultiMap.get(id);

            if (books == null || books.isEmpty()) {
                log.error("Book not found in Hazelcast datalake MultiMap: {}", bookId);
                throw new RuntimeException("Book not found in Hazelcast: " + bookId);
            }

            ReplicatedBook book = books.iterator().next();
            log.info("Retrieved book {} from Hazelcast datalake MultiMap", bookId);
            return book.getBody();
        } catch (NumberFormatException e) {
            log.error("Invalid book ID format: {}", bookId, e);
            throw new RuntimeException("Invalid book ID format: " + bookId, e);
        }
    }
}
