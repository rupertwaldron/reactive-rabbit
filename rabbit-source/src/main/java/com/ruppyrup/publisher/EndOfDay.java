package com.ruppyrup.publisher;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class EndOfDay {
    private static final String QUEUE_NAME1 = "myqueue2";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    public static void main(String[]args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            try {
                String now = LocalDateTime.now().format(formatter);
                Map<String, Object> headers = Map.of("endOfDayTime", now);
                AMQP.BasicProperties build = new AMQP.BasicProperties.Builder().headers(headers).build();
                channel.basicPublish("", QUEUE_NAME1, build, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
