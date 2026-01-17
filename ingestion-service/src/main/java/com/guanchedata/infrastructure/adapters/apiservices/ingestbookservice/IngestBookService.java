package com.guanchedata.infrastructure.adapters.apiservices.ingestbookservice;

import com.guanchedata.infrastructure.ports.BookDownloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Path;
import java.util.Map;

public class IngestBookService implements BookDownloader {

    private static final Logger log = LoggerFactory.getLogger(IngestBookService.class);
    private final IngestBookInfrastructure infra;

    public IngestBookService(IngestBookInfrastructure infra) {
        this.infra = infra;
    }

    @Override
    public Map<String, Object> ingestBook(int bookId) {
        log.info("ingest() - Start processing bookId={}", bookId);
        try {
            if (infra.downloadLog.isDownloaded(bookId)) {
                return alreadyDownloadedResponse(bookId);
            }

            String[] content = infra.gutenberg.getBook(bookId);
            Path savedPath = infra.storage.saveBook(bookId, content);
            infra.hazelcast.uploadBookToMemory(bookId, content);
            infra.hazelcast.getHazelcastReplicationExecuter().execute(bookId);
            infra.downloadLog.registerBookDownload(bookId);
            infra.notifier.notifyIngestedBook(bookId);
            return successResponse(bookId, savedPath);

        } catch (Exception e) {
            return errorResponse(bookId, e);
        } finally {
            log.info("ingest() - Finished processing bookId={}", bookId);
        }
    }

    private Map<String, Object> alreadyDownloadedResponse(int bookId) {
        log.warn("ingest() - Book {} is already downloaded, skipping ingestion", bookId);
        return Map.of(
                "book_id", bookId,
                "status", "already_downloaded",
                "message", "Book already exists in datalake"
        );
    }

    private Map<String, Object> successResponse(int bookId, Path savedPath) {
        log.info("ingest() - Book {} downloaded and saved at {}", bookId, savedPath);
        return Map.of(
                "book_id", bookId,
                "status", "downloaded",
                "path", savedPath.toString().replace("\\", "/")
        );
    }

    private Map<String, Object> errorResponse(int bookId, Exception e) {
        log.error("ingest() - Error processing bookId {}: {}", bookId, e.getMessage(), e);
        return Map.of(
                "book_id", bookId,
                "status", "error",
                "message", e.getMessage()
        );
    }
}
