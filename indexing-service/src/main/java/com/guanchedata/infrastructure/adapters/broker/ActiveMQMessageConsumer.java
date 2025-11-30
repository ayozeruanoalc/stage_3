package com.guanchedata.infrastructure.adapters.broker;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.guanchedata.infrastructure.ports.MessageConsumer;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;
import java.util.function.Consumer;

public class ActiveMQMessageConsumer implements MessageConsumer {
    private static final Logger log = LoggerFactory.getLogger(ActiveMQMessageConsumer.class);
    private final String brokerUrl;
    private final String queueName;
    private final Gson gson = new Gson();
    private Connection connection;
    private Session session;
    private javax.jms.MessageConsumer consumer;

    public ActiveMQMessageConsumer(String brokerUrl, String queueName) {
        this.brokerUrl = brokerUrl;
        this.queueName = queueName;
    }

    @Override
    public void startConsuming(Consumer<String> messageHandler) {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
            connection = factory.createConnection();
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination queue = session.createQueue(queueName);
            consumer = session.createConsumer(queue);

            consumer.setMessageListener(message -> {
                try {
                    if (message instanceof TextMessage) {
                        String text = ((TextMessage) message).getText();
                        log.info("Received message from broker: {}", text);
                        String bookId = extractBookId(text);
                        messageHandler.accept(bookId);
                    }
                } catch (JMSException e) {
                    log.error("Error processing message: {}", e.getMessage(), e);
                }
            });

            log.info("ActiveMQ consumer started on queue: {}", queueName);

        } catch (JMSException e) {
            log.error("Error starting ActiveMQ consumer: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to start message consumer", e);
        }
    }

    private String extractBookId(String jsonMessage) {
        try {
            JsonObject json = gson.fromJson(jsonMessage, JsonObject.class);
            int bookId = json.get("bookId").getAsInt();
            return String.valueOf(bookId);
        } catch (Exception e) {
            log.warn("Failed to parse JSON, using raw message: {}", jsonMessage);
            return jsonMessage;
        }
    }

    @Override
    public void stopConsuming() {
        try {
            if (consumer != null) consumer.close();
            if (session != null) session.close();
            if (connection != null) connection.close();
            log.info("ActiveMQ consumer stopped");
        } catch (JMSException e) {
            log.error("Error stopping ActiveMQ consumer: {}", e.getMessage(), e);
        }
    }
}
