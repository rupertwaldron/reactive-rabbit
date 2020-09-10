package com.ruppyrup.publisher;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.json.simple.JSONObject;
import rabbit.models.Person;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

public class Publisher {

    private static final String QUEUE_NAME1 = "aName";
    public static final int END_INCLUSIVE = 1000;

    public static void main(String[]args) throws IOException, TimeoutException, InterruptedException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //channel.queueDeclare(QUEUE_NAME1, true, false, false, null);

        Instant start = Instant.now();
        IntStream.rangeClosed(1, END_INCLUSIVE)
                .mapToObj(Person::new)
                .map(person -> {
                    try {
                        JSONObject object = new JSONObject();
                        object.put("name", person.getName());
                        object.put("age", person.getAge());
                        object.put("city", person.getCity());
                        return object.toJSONString().getBytes();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return new byte[0];
                })
                .forEach(personBytes -> {
            try {
                channel.basicPublish("", QUEUE_NAME1, null, personBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
                    System.out.println(" [x] Sent '" + new String(personBytes) + "'");
                });

        Instant finish = Instant.now();

        System.out.println("Time to publish = " + Duration.between(start, finish).toMillis());

        channel.close();
        connection.close();
    }
}
