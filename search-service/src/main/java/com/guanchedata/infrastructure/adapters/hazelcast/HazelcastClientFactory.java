package com.guanchedata.infrastructure.adapters.hazelcast;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientClasspathXmlConfig;
import com.hazelcast.core.HazelcastInstance;

public class HazelcastClientFactory {
    public static HazelcastInstance hazelcastInstance;

    public static HazelcastInstance getClient() {
        if (hazelcastInstance == null) {
            ClientConfig config = new ClientClasspathXmlConfig("hazelcast-client.xml");
            hazelcastInstance = HazelcastClient.newHazelcastClient(config);
        }
        return hazelcastInstance;
    }

    private HazelcastClientFactory() {}
}
