package com.guanchedata.infrastructure.adapters.hazelcast;

import com.guanchedata.infrastructure.ports.ReplicationExecuter;
import com.guanchedata.model.NodeInfoProvider;
import com.guanchedata.model.ReplicatedBook;
import com.hazelcast.collection.IQueue;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.lock.FencedLock;
import com.hazelcast.multimap.MultiMap;

public class HazelcastReplicationExecuter implements ReplicationExecuter {

    private final HazelcastInstance hazelcast;
    private final NodeInfoProvider nodeInfoProvider;
    private final int replicationFactor;

    public HazelcastReplicationExecuter(HazelcastInstance hazelcast, NodeInfoProvider nodeInfoProvider, int replicationFactor) {
        this.hazelcast = hazelcast;
        this.nodeInfoProvider = nodeInfoProvider;
        this.replicationFactor = replicationFactor;
    }

    public void execute(int bookId) {
        addBookLocation(bookId);
        replicate(bookId);
    }

    @Override
    public void replicate(int bookId) {
        //FencedLock lock = hazelcast.getCPSubsystem().getLock("lock:book:" + bookId);
        //lock.lock();
        IQueue<ReplicatedBook> booksToBeReplicated = hazelcast.getQueue("booksToBeReplicated");
        try {
            for (int i = 1; i < replicationFactor; i++) {
                booksToBeReplicated.put(new ReplicatedBook(bookId, this.nodeInfoProvider.getNodeId()));
            }
            System.out.println(booksToBeReplicated);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            //lock.unlock();
        }
    }

    public void addBookLocation(int bookId) {
        MultiMap<Integer,NodeInfoProvider> bookLocations = this.hazelcast.getMultiMap("bookLocations");
        bookLocations.put(bookId, this.nodeInfoProvider);
    }

}
