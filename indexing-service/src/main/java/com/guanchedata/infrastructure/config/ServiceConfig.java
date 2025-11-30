package com.guanchedata.infrastructure.config;

public class ServiceConfig {
    private final String brokerUrl;
    private final String clusterName;
    private final int port;

    public ServiceConfig() {
        this.brokerUrl = System.getenv().getOrDefault("BROKER_URL", "tcp://activemq:61616");
        this.clusterName = System.getenv().getOrDefault("HAZELCAST_CLUSTER_NAME", "SearchEngine");
        this.port = 7002;
    }

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public String getClusterName() {
        return clusterName;
    }

    public int getPort() {
        return port;
    }
}
