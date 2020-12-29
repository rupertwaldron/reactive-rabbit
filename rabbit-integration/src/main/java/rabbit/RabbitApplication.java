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
import reactor.core.publisher.ParallelFlux;
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

        ParallelFlux<PersonDto> parallelFlux = RabbitFlux.createReceiver(receiverOptions)
                .consumeNoAck(QUEUE_NAME)
                .parallel()
                .map(message -> messageConverter.extractReactiveObject(message.getBody()));

//                parallelFlux
//                .flatMap(personDto -> starService.getWebClientStars(personDto))
//                .doOnNext(personService::addPerson)
//                .subscribe(personDto -> System.out.println("Person :: " + personDto));


        rSocketRequester
                .route("person.stars")
                .data(parallelFlux)
                .retrieveFlux(PersonDto.class)
                .map(personDto -> {
                    personDto.setAge(15);
                    return personDto;
                })
                .doOnNext(personService::addPerson)
                .subscribe(personDto -> System.out.println("Person :: " + personDto));




//        inboundFlux
//                .map(message -> messageConverter.extractReactiveObject(message.getBody()))
//                .subscribe(person -> System.out.println("Reactive Received :: " + person));




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
