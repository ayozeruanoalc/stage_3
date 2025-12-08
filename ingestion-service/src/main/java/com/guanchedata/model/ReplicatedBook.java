package com.guanchedata.model;

import java.io.Serializable;

public class ReplicatedBook implements Serializable {
    private final int id;
    private final String sourceNode;

    public ReplicatedBook(int id, String sourceNode) {
        this.id = id;
        this.sourceNode = sourceNode;
    }

    public int getId() {
        return this.id;
    }

    public String getSourceNode() {
        return this.sourceNode;
    }
}
