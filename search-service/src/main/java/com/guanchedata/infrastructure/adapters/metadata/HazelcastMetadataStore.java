package com.guanchedata.infrastructure.adapters.metadata;

import com.guanchedata.infrastructure.ports.MetadataStore;
import com.guanchedata.model.BookMetadata;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class HazelcastMetadataStore implements MetadataStore {
    private static final Logger log = LoggerFactory.getLogger(HazelcastMetadataStore.class);
    private final IMap<Integer, BookMetadata> metadataMap;

    public HazelcastMetadataStore(HazelcastInstance hazelcastInstance) {
        this.metadataMap = hazelcastInstance.getMap("bookMetadata");
    }

    @Override
    public BookMetadata getMetadata(String bookId) {
        return metadataMap.get(Integer.parseInt(bookId));
    }

    @Override
    public Map<Integer, BookMetadata> getMetadataBulk(Set<Integer> bookIds) {
        return metadataMap.getAll(bookIds);
    }
}