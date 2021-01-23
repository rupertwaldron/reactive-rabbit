package com.ruppyrup.publisher;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

@Slf4j
public class Publisher {

    private static final String QUEUE_NAME1 = "myqueue2";
    public static final int END_INCLUSIVE = 10;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

    public static void main(String[]args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Instant start = Instant.now();

        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            IntStream.rangeClosed(1, END_INCLUSIVE)
                    .mapToObj(Person::new)
                    .map(person -> {
                        try {
                            JSONObject object = new JSONObject();
                            object.put("name", person.getName());
                            object.put("age", person.getAge());
                            object.put("city", person.getCity());
//                        TimeUnit.SECONDS.sleep(1);
                            return object.toJSONString().getBytes();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return new byte[0];
                    })
                    .forEach(personBytes -> {
                        try {
                            String now = LocalDateTime.now().format(formatter);
                            Map<String, Object> headers = Map.of("sendTime", now);

                            AMQP.BasicProperties build = new AMQP.BasicProperties.Builder().headers(headers).build();
                            channel.basicPublish("", QUEUE_NAME1, build, personBytes);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println(" [x] Sent '" + new String(personBytes) + "'");
                    });
        } finally {
            Instant finish = Instant.now();
            log.info("Time to publish = " + Duration.between(start, finish).toMillis());
        }


    }
}
