package com.guanchedata.infrastructure.adapters.web;

public interface IndexingStatusStore {
    boolean markAsIndexed(int documentId);
}