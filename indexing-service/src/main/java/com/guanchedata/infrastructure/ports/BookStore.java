package com.guanchedata.infrastructure.ports;

import com.guanchedata.model.BookContent;

public interface BookStore {
    BookContent getBookContent(int bookId);
    void save(int bookId, BookContent content);
}