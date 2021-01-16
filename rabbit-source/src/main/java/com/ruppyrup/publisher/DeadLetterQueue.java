package com.ruppyrup.publisher;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class DeadLetterQueue {
    private static final String QUEUE_NAME1 = "myqueue1";
    private static final String EXCHANGE = "myexchange1";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            String now = LocalDateTime.now().format(formatter);
            Map<String, Object> headers = Map.of("error", now);
            AMQP.BasicProperties build = new AMQP.BasicProperties.Builder().headers(headers).build();
            channel.basicPublish(EXCHANGE, "test.myfirsttest", build, "Hello from rupert".getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

@Slf4j
class SubscribeToQueue {

    private static final String EXCHANGE = "myexchange1";
    private static final String QUEUE_NAME1 = "myqueue1";
    private static final String QUEUE = "aName";

    public static void main(String[] args) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.useNio();
        factory.setHost("localhost");


        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            channel.basicConsume(QUEUE, true, deliverCallback, consumerTag -> { });
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }



//
//        ReceiverOptions receiverOptions = new ReceiverOptions()
//                .connectionFactory(factory)
//                .connectionSubscriptionScheduler(Schedulers.boundedElastic());
//
//        System.out.println("Finding message has started");

//
//        try (Receiver receiver = RabbitFlux.createReceiver()) {
//            receiver
//                    .consumeAutoAck(QUEUE)
//                    .map(Delivery::getBody)
//                    .subscribe(delivery -> log.info("Received Message :: " + delivery));
//        }


    }

    static DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), "UTF-8");
        System.out.println(" [x] Received '" + message + "'");
    };

}

