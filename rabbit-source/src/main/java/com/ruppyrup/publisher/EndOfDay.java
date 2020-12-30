package com.ruppyrup.publisher;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

public class EndOfDay {
    private static final String QUEUE_NAME1 = "aName";

    public static void main(String[]args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            try {
                String now = LocalDateTime.now().toString();
                Map<String, Object> headers = Map.of("endOfDayTime", now);
                AMQP.BasicProperties build = new AMQP.BasicProperties.Builder().headers(headers).build();
                channel.basicPublish("", QUEUE_NAME1, build, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
