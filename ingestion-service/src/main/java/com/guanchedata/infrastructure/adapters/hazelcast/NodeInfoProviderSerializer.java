package com.guanchedata.infrastructure.adapters.hazelcast;

import com.guanchedata.model.NodeInformation;
import com.hazelcast.nio.serialization.compact.CompactReader;
import com.hazelcast.nio.serialization.compact.CompactSerializer;
import com.hazelcast.nio.serialization.compact.CompactWriter;

public class NodeInfoProviderSerializer implements CompactSerializer<NodeInformation> {

    @Override
    public NodeInformation read(CompactReader reader) {
        String nodeId = reader.readString("nodeId");
        return new NodeInformation(nodeId);
    }

    @Override
    public void write(CompactWriter writer, NodeInformation nodeInformation) {
        writer.writeString("nodeId", nodeInformation.getNodeId());
    }

    @Override
    public String getTypeName() {
        return "NodeInfoProvider";
    }

    @Override
    public Class<NodeInformation> getCompactClass() {
        return NodeInformation.class;
    }
}