package com.guanchedata.infrastructure.config;

import com.guanchedata.infrastructure.adapters.broker.ActiveMQMessageConsumer;
import com.guanchedata.infrastructure.adapters.broker.RebuildMessageListener;
import com.guanchedata.infrastructure.adapters.web.IndexBook;
import com.guanchedata.infrastructure.ports.MessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageBrokerConfig {
    private static final Logger log = LoggerFactory.getLogger(MessageBrokerConfig.class);

    public MessageConsumer createConsumer(String brokerUrl, IndexBook indexBook, RebuildMessageListener rebuildListener) {
        MessageConsumer messageConsumer = new ActiveMQMessageConsumer(
                brokerUrl,
                "documents.ingested",
                rebuildListener
        );

        messageConsumer.startConsuming(documentId -> {
            log.info("Processing document from broker: {}", documentId);
            indexBook.execute(Integer.parseInt(documentId));
        });

        return messageConsumer;
    }
}