package com.guanchedata.infrastructure.adapters.hazelcast;

import com.guanchedata.infrastructure.adapters.filesystem.BookStorageDate;
import com.guanchedata.infrastructure.config.HazelcastConfig;
import com.guanchedata.infrastructure.ports.BookProvider;
import com.guanchedata.model.BookContent;
import com.guanchedata.model.NodeInfoProvider;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

public class HazelcastManager {

    HazelcastInstance hazelcastInstance;
    NodeInfoProvider nodeInfoProvider;
    HazelcastDatalakeListener hazelcastDatalakeListener;
    HazelcastReplicationExecuter hazelcastReplicationExecuter;

    public HazelcastManager(String clusterName, int replicationFactor, BookProvider bookProvider, BookStorageDate bookStorageDate) {
        this.nodeInfoProvider = new NodeInfoProvider(System.getenv("HZ_PUBLIC_ADDRESS"));
        this.hazelcastInstance = new HazelcastConfig().initHazelcast(clusterName);

        this.hazelcastDatalakeListener = new HazelcastDatalakeListener(this.hazelcastInstance,
                this.nodeInfoProvider, bookProvider, bookStorageDate);

        this.hazelcastDatalakeListener.registerListener();

        this.hazelcastReplicationExecuter = new HazelcastReplicationExecuter(this.hazelcastInstance,
                this.nodeInfoProvider, replicationFactor);
    }

    public void uploadBookToMemory(int bookId, String[] contentSeparated) {
        String header = contentSeparated[0];
        String body = contentSeparated[1];
        IMap<Integer, BookContent> datalake = this.hazelcastInstance.getMap("datalake");
        datalake.put(bookId, new BookContent(header, body));
    }

    public HazelcastInstance getHazelcastInstance() {
        return this.hazelcastInstance;
    }

    public HazelcastReplicationExecuter getHazelcastReplicationExecuter() {
        return hazelcastReplicationExecuter;
    }
}