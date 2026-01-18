package com.guanchedata.infrastructure.adapters.hazelcast;

import com.guanchedata.infrastructure.ports.Datalake;
import com.guanchedata.model.BookContent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

public class HazelcastDatalake implements Datalake {

    private final HazelcastInstance hz;
    private final HazelcastReplicationExecuter replicator;

    public HazelcastDatalake(HazelcastInstance hz, HazelcastReplicationExecuter replicator) {
        this.hz = hz;
        this.replicator = replicator;
    }

    @Override
    public void save(int bookId, BookContent content) {
        IMap<Integer, BookContent> map = hz.getMap("datalake");
        map.put(bookId, content);
    }

    @Override
    public void replicate(int bookId) {
        replicator.execute(bookId);
    }
}