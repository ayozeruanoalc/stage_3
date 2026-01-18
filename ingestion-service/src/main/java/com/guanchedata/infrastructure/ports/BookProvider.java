package com.guanchedata.infrastructure.ports;

public interface BookProvider {
    String[] getBookContent(int bookId);
}