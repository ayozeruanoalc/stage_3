package com.guanchedata.util;

import com.guanchedata.infrastructure.adapters.bookprovider.GutenbergConnection;
import com.guanchedata.infrastructure.adapters.bookprovider.GutenbergFetch;

public class GutenbergBookDownloader {

    private final GutenbergFetch gutenbergFetch;
    private final GutenbergConnection gutenbergConnection;

    public GutenbergBookDownloader(GutenbergFetch gutenbergFetch, GutenbergConnection gutenbergConnection) {
        this.gutenbergFetch = gutenbergFetch;
        this.gutenbergConnection = gutenbergConnection;
    }

    public String fetchBook(int bookId) throws Exception {
        GutenbergConnection connection = new GutenbergConnection();
        GutenbergFetch fetch = new GutenbergFetch();
        return fetch.fetchBook(connection.createConnection(bookId));
    }
}
