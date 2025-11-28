package com.guanchedata.infrastructure.adapters.hazelcast;

import com.guanchedata.model.NodeInfoProvider;
import com.hazelcast.core.HazelcastInstance;

public class HazelcastReplicationManager {

    HazelcastInstance hazelcastInstance;
    NodeInfoProvider nodeInfoProvider;
    HazelcastDatalakeListener hazelcastDatalakeListener;
    HazelcastReplicationExecuter hazelcastReplicationExecuter;

    public HazelcastReplicationManager(String clusterName, int replicationFactor) {
        this.nodeInfoProvider = new NodeInfoProvider(System.getenv("CRAWLER_ID")); // NODE IDENTIFIER
        this.hazelcastInstance = new HazelcastConfig().initHazelcast(clusterName); // CREATE HAZELCAST MEMBER
        this.hazelcastDatalakeListener = new HazelcastDatalakeListener(this.hazelcastInstance,this.nodeInfoProvider,replicationFactor);
        // (above) ADD LISTENER
        this.hazelcastReplicationExecuter = new HazelcastReplicationExecuter(this.hazelcastInstance, this.nodeInfoProvider);
    }

    public HazelcastReplicationExecuter getHazelcastReplicationExecuter() {
        return hazelcastReplicationExecuter;
    }
}
