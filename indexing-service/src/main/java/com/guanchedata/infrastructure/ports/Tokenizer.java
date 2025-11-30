package com.guanchedata.infrastructure.ports;

import java.util.Set;

public interface Tokenizer {
    Set<String> tokenize(String text);
}
