package com.guanchedata.infrastructure.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.util.Arrays;

public class HazelcastConfig {

    public HazelcastInstance initHazelcast(String clusterName) {
        Config config = new Config();
        config.setClusterName(clusterName);

        String publicIp = System.getenv("PUBLIC_IP");
        String hzPort   = System.getenv("HZ_PORT");

        NetworkConfig networkConfig = config.getNetworkConfig();

        networkConfig.setPort(Integer.parseInt(hzPort));
        networkConfig.setPortAutoIncrement(false);
        networkConfig.setPublicAddress(publicIp + ":" + hzPort);

        JoinConfig join = networkConfig.getJoin();
        join.getMulticastConfig().setEnabled(false);
        join.getAutoDetectionConfig().setEnabled(false);
        join.getTcpIpConfig()
                .setEnabled(true)
                .setMembers(Arrays.asList(
                        ":5701", //IP
                        ":5702" //IP
                ));

        config.setProperty("hazelcast.wait.seconds.before.join", "0");
        config.setProperty("hazelcast.socket.bind.any", "true");

        return Hazelcast.newHazelcastInstance(config);
    }
}
