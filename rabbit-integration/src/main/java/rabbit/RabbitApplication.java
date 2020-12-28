package rabbit;

import com.rabbitmq.client.*;
import org.json.simple.JSONObject;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.Message;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.scheduling.annotation.EnableAsync;
import rabbit.config.RabbitConfig;
import rabbit.models.PersonDto;
import rabbit.service.PersonService;
import rabbit.service.StarService;
import rabbit.transformers.MessageConverter;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.ReceiverOptions;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.stream.IntStream;

@SpringBootApplication
@EnableAsync
public class RabbitApplication implements CommandLineRunner {

    private static final String QUEUE_NAME = "aName";

    @Autowired
    StarService starService;

    @Autowired
    PersonService personService;

    @Autowired
    MessageConverter messageConverter;

    @Autowired
    private RSocketRequester rSocketRequester;

    public Publisher<Message<PersonDto>> amqpPublisher;

    public static void main(String[] args) {
//        new SpringApplicationBuilder(RabbitApplication.class)
//                .web(WebApplicationType.NONE)
//                .run(args);
        SpringApplication.run(RabbitApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.useNio();
        factory.setHost("localhost");

        ReceiverOptions receiverOptions =  new ReceiverOptions()
                .connectionFactory(factory)
                .connectionSubscriptionScheduler(Schedulers.boundedElastic());

        Flux<Delivery> inboundFlux = RabbitFlux.createReceiver(receiverOptions)
                .consumeNoAck(QUEUE_NAME);

        inboundFlux
                .map(message -> messageConverter.extractReactiveObject(message.getBody()))
                .subscribe(person -> System.out.println("Reactive Received :: " + person));

//        com.rabbitmq.client.ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("localhost");
//        Connection connection = factory.newConnection();
//        Channel channel = connection.createChannel();
//
//        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//            String message = new String(delivery.getBody(), "UTF-8");
//            System.out.println(" [x] Received '" + message + "'");
//        };
//
//        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });

        //channel.queueDeclare(QUEUE_NAME1, true, false, false, null);

//        Instant start = Instant.now();
//        IntStream.rangeClosed(1, END_INCLUSIVE)
//                .mapToObj(Person::new)
//                .map(person -> {
//                    try {
//                        JSONObject object = new JSONObject();
//                        object.put("name", person.getName());
//                        object.put("age", person.getAge());
//                        object.put("city", person.getCity());
//                        return object.toJSONString().getBytes();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    return new byte[0];
//                })
//                .forEach(personBytes -> {
//                    try {
//                        String now = LocalDateTime.now().toString();
//                        Map<String, Object> headers = Map.of("sendTime", now);
//
//                        AMQP.BasicProperties build = new AMQP.BasicProperties.Builder().headers(headers).build();
//                        channel.basicPublish("", QUEUE_NAME1, build, personBytes);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    System.out.println(" [x] Sent '" + new String(personBytes) + "'");
//                });
//
//        Instant finish = Instant.now();
//
//        System.out.println("Time to publish = " + Duration.between(start, finish).toMillis());

//        channel.close();
//        connection.close();





//
//        Flux<PersonDto> personDtoFlux = Flux.from(amqpPublisher)
//                .map(Message::getPayload);
//
//        starService.getWebClientStars(personDtoFlux)
//                .map(personDto -> {
//                    personDto.setAge(15);
//                    return personDto;
//                })
//                .doOnNext(personService::addPerson)
//                .subscribe(personDto -> System.out.println("Person :: " + personDto));

//        rSocketRequester
//                .route("person.stars")
//                .data(personDtoFlux)
//                .retrieveFlux(PersonDto.class)
//                .map(personDto -> {
//                    personDto.setAge(15);
//                    return personDto;
//                })
//                .doOnNext(personService::addPerson)
//                .subscribe(personDto -> System.out.println("Person :: " + personDto));
    }
}
