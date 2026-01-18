package com.guanchedata.infrastructure.ports;

public interface IndexingStatusStore {
    boolean markAsIndexed(int documentId);
}