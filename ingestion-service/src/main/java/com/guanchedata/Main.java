package com.guanchedata;

import com.guanchedata.application.usecases.ingestionservice.BookIngestionPeriodicExecutor;
import com.guanchedata.application.usecases.ingestionservice.BookProviderController;
import com.guanchedata.infrastructure.adapters.activemq.ActiveMQBookIngestedNotifier;
import com.guanchedata.infrastructure.adapters.apiservices.BookStatusService;
import com.guanchedata.infrastructure.adapters.apiservices.IngestBookService;
import com.guanchedata.infrastructure.adapters.apiservices.ListBooksService;
import com.guanchedata.infrastructure.adapters.bookprovider.*;
import com.guanchedata.infrastructure.adapters.hazelcast.DatalakeRecoveryNotifier;
import com.guanchedata.infrastructure.adapters.hazelcast.HazelcastReplicationManager;
import com.guanchedata.infrastructure.ports.*;
import com.guanchedata.util.DateTimePathGenerator;
import com.guanchedata.util.GutenbergBookDownloader;
import io.javalin.Javalin;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        System.out.println(args[0]);

        BookDownloadStatusStore bookDownloadLog = new BookDownloadLog(args[1]);
        PathGenerator pathGenerator = new DateTimePathGenerator(args[0]);
        GutenbergBookContentSeparator separator = new GutenbergBookContentSeparator();


        BookStorageDate storageDate = new BookStorageDate(pathGenerator, separator);
        GutenbergBookDownloader gutenbergBookDownloader = new GutenbergBookDownloader(new GutenbergFetch(), new GutenbergConnection());

        ActiveMQBookIngestedNotifier notifier =  new ActiveMQBookIngestedNotifier(System.getenv("BROKER_URL"));
        HazelcastReplicationManager hazelcastManager = new HazelcastReplicationManager("SearchEngine", Integer.parseInt(System.getenv("REPLICATION_FACTOR")), gutenbergBookDownloader, storageDate);
        BookDownloader ingestBookService = new IngestBookService(storageDate, bookDownloadLog, notifier, hazelcastManager, gutenbergBookDownloader);


        BookListProvider listBooksService = new ListBooksService(bookDownloadLog);
        BookStatusProvider bookStatusService = new BookStatusService(bookDownloadLog);

        BookProviderController controller = new BookProviderController(
                ingestBookService,
                listBooksService,
                bookStatusService
        );

        DatalakeRecoveryNotifier recovery = new DatalakeRecoveryNotifier(hazelcastManager.getHazelcastInstance(), hazelcastManager.getNodeInfoProvider(), notifier);
        recovery.reloadDatalakeFromDisk(args[0]);

        BookIngestionPeriodicExecutor bookIngestionExecutor = new BookIngestionPeriodicExecutor(hazelcastManager.getHazelcastInstance(),ingestBookService);

        Javalin app = Javalin.create(config -> {
            config.http.defaultContentType = "application/json";
        }).start(7001);

        bookIngestionExecutor.setupBookQueue();
        bookIngestionExecutor.startPeriodicExecution();

        app.post("/ingest/{book_id}", controller::ingestBook);
        app.get("/ingest/status/{book_id}", controller::status);
        app.get("/ingest/list", controller::listAllBooks);
    }
}

