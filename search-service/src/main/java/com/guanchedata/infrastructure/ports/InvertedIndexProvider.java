package com.guanchedata.infrastructure.ports;
import java.util.Collection;

public interface InvertedIndexProvider {
    Collection<String> getDocuments(String token);
}
