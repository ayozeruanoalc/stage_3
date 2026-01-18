package com.guanchedata.infrastructure.ports;

import com.guanchedata.model.BookContent;

public interface Datalake {
    void save(int bookId, BookContent content);
    void replicate(int bookId);
}