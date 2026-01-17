package com.guanchedata.infrastructure.ports;

public interface BookIngestedNotifier {
    void notifyIngestedBook(int bookId);
}
