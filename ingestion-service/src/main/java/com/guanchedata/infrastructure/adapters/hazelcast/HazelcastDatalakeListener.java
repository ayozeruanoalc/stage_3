package com.guanchedata.infrastructure.adapters.hazelcast;

import com.guanchedata.model.NodeInfoProvider;
import com.guanchedata.model.ReplicatedBook;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.multimap.MultiMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HazelcastDatalakeListener extends AbstractEntryListener<Integer, ReplicatedBook> {

    private final NodeInfoProvider nodeInfoProvider;
    private final int replicationFactor;
    private final HazelcastInstance hazelcast;

    public HazelcastDatalakeListener(HazelcastInstance hazelcast,
                                     NodeInfoProvider nodeInfoProvider,
                                     int replicationFactor) {
        this.hazelcast = hazelcast;
        this.nodeInfoProvider = nodeInfoProvider;
        this.replicationFactor = replicationFactor;
    }

    public void register() {
        MultiMap<Integer, ReplicatedBook> datalake = hazelcast.getMultiMap("datalake");
        datalake.addEntryListener(this, true);
    }

    @Override
    public void entryAdded(EntryEvent<Integer, ReplicatedBook> event) {
        ReplicatedBook replicated = event.getValue();
        int bookId = event.getKey();

        if (replicated.getSourceNode().equals(nodeInfoProvider.getNodeId())) return;

        IMap<Integer, Integer> replicaCount = hazelcast.getMap("replication-count");
        int current = replicaCount.getOrDefault(bookId, 0);
        if (current >= replicationFactor) return;
        replicaCount.put(bookId, current + 1);

        try {
            Files.writeString(Paths.get("/app/datalake/" + bookId + "_header.txt"), replicated.getHeader());
            Files.writeString(Paths.get("/app/datalake/" + bookId + "_body.txt"), replicated.getBody());
            System.out.println("Libro " + bookId + "replicado en nodo " + this.nodeInfoProvider.getNodeId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
