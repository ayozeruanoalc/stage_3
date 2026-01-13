package com.guanchedata.infrastructure.ports;

import com.guanchedata.model.BookMetadata;
import java.util.Map;
import java.util.Set;

public interface MetadataStore {
    BookMetadata getMetadata(String bookId);
    Map<Integer, BookMetadata> getMetadataBulk(Set<Integer> bookIds);
}