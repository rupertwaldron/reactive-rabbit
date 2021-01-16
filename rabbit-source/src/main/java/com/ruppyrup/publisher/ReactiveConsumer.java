package com.ruppyrup.publisher;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Delivery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;


@Slf4j
public class ReactiveConsumer {
    private static final String QUEUE_NAME = "myqueue1";

    public static void main(String[] args) throws InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.useNio();
        factory.setHost("localhost");

        ReceiverOptions receiverOptions = new ReceiverOptions()
                .connectionFactory(factory)
                .connectionSubscriptionScheduler(Schedulers.boundedElastic());

        try (Receiver receiver = RabbitFlux.createReceiver(receiverOptions)) {
            receiver
                    .consumeManualAck(QUEUE_NAME)
                    .subscribe(message -> {
//                        message.ack();
                        log.info("Found message :: " + message.getBody());

                    });

            TimeUnit.SECONDS.sleep(20);
        } catch (Exception ex) {
            log.error(ex.getLocalizedMessage());
        }



    }
}

