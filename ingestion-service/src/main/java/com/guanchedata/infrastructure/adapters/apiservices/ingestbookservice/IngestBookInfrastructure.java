package com.guanchedata.infrastructure.adapters.apiservices.ingestbookservice;

import com.guanchedata.infrastructure.adapters.activemq.ActiveMQBookIngestedNotifier;
import com.guanchedata.infrastructure.adapters.hazelcast.HazelcastManager;
import com.guanchedata.infrastructure.ports.BookDownloadStatusStore;
import com.guanchedata.infrastructure.ports.BookStorage;
import com.guanchedata.util.GutenbergBookProvider;

public final class IngestBookInfrastructure {

    public final BookStorage storage;
    public final BookDownloadStatusStore downloadLog;
    public final ActiveMQBookIngestedNotifier notifier;
    public final HazelcastManager hazelcast;
    public final GutenbergBookProvider gutenberg;

    public IngestBookInfrastructure(BookStorage storage, BookDownloadStatusStore downloadLog,
            ActiveMQBookIngestedNotifier notifier, HazelcastManager hazelcast, GutenbergBookProvider gutenberg) {

        this.storage = storage;
        this.downloadLog = downloadLog;
        this.notifier = notifier;
        this.hazelcast = hazelcast;
        this.gutenberg = gutenberg;
    }
}
