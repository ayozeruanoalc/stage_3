package com.guanchedata.model;

import java.time.Instant;

public class IngestionControlEvent {

    public enum Type{
        INGESTION_PAUSE,
        INGESTION_RESUME
    }

    private final String event = "ingestion.control";
    private final String ts = Instant.now().toString();
    private Type type;

    public IngestionControlEvent(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public String getTs() {
        return ts;
    }
}
