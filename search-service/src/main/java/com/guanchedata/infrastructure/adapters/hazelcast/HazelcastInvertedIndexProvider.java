package com.guanchedata.infrastructure.adapters.hazelcast;

import com.guanchedata.infrastructure.ports.InvertedIndexProvider;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.multimap.MultiMap;

import java.util.Collection;

public class HazelcastInvertedIndexProvider implements InvertedIndexProvider {
    private final MultiMap<String, String> index;

    public HazelcastInvertedIndexProvider(HazelcastInstance hazelcastInstance) {
        this.index = hazelcastInstance.getMultiMap("inverted-index");
    }

    @Override
    public Collection<String> getDocuments(String token) {
        return index.get(token);
    }
}
