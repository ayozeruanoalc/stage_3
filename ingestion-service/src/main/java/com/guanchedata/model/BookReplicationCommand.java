package com.guanchedata.model;

import java.io.Serializable;
import java.util.List;

public class BookReplicationCommand implements Serializable {
    private final int id;
    private final List<String> sourceNode;

    public BookReplicationCommand(int id, List<String> sourceNode) {
        this.id = id;
        this.sourceNode = sourceNode;
    }

    public int getId() {
        return this.id;
    }

    public List<String> getAlreadyReplicatedNodes() {
        return this.sourceNode;
    }
}
