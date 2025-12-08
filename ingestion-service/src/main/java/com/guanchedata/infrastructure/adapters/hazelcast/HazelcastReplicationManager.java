package com.guanchedata.infrastructure.adapters.hazelcast;

import com.guanchedata.infrastructure.adapters.apiservices.IngestBookService;
import com.guanchedata.infrastructure.adapters.bookprovider.BookStorageDate;
import com.guanchedata.model.NodeInfoProvider;
import com.guanchedata.util.GutenbergBookDownloader;
import com.hazelcast.core.HazelcastInstance;

public class HazelcastReplicationManager {

    HazelcastInstance hazelcastInstance;
    NodeInfoProvider nodeInfoProvider;
    HazelcastDatalakeListener hazelcastDatalakeListener;
    HazelcastReplicationExecuter hazelcastReplicationExecuter;

    public HazelcastReplicationManager(String clusterName, int replicationFactor, GutenbergBookDownloader gutenbergBookDownloader, BookStorageDate bookStorageDate) {
        this.nodeInfoProvider = new NodeInfoProvider(System.getenv("PUBLIC_IP")); // NODE IDENTIFIER
        this.hazelcastInstance = new HazelcastConfig().initHazelcast(clusterName); // CREATE HAZELCAST MEMBER
        this.hazelcastDatalakeListener = new HazelcastDatalakeListener(this.hazelcastInstance,this.nodeInfoProvider, gutenbergBookDownloader, bookStorageDate);
        this.hazelcastDatalakeListener.registerListener();
        this.hazelcastReplicationExecuter = new HazelcastReplicationExecuter(this.hazelcastInstance, this.nodeInfoProvider,replicationFactor);
    }


    public HazelcastInstance getHazelcastInstance() {
        return this.hazelcastInstance;
    }

    public NodeInfoProvider getNodeInfoProvider() {
        return this.nodeInfoProvider;
    }

    public HazelcastReplicationExecuter getHazelcastReplicationExecuter() {
        return hazelcastReplicationExecuter;
    }
}
