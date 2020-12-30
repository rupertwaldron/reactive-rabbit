package rabbit;

import com.rabbitmq.client.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import rabbit.models.PersonDto;
import rabbit.service.PersonService;
import rabbit.service.StarService;
import rabbit.transformers.MessageConverter;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.ReceiverOptions;

@SpringBootApplication
@EnableAsync
public class RabbitApplication implements CommandLineRunner {

    private static final String QUEUE_NAME = "aName";

    private final StarService starService;

    private final PersonService personService;

    private final MessageConverter messageConverter;

    public RabbitApplication(StarService starService,
                             PersonService personService,
                             MessageConverter messageConverter) {
        this.starService = starService;
        this.personService = personService;
        this.messageConverter = messageConverter;
    }

    public static void main(String[] args) {
        SpringApplication.run(RabbitApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.useNio();
        factory.setHost("localhost");

        ReceiverOptions receiverOptions = new ReceiverOptions()
                .connectionFactory(factory)
                .connectionSubscriptionScheduler(Schedulers.boundedElastic());

        Flux<PersonDto> parallelFlux = RabbitFlux.createReceiver(receiverOptions)
                .consumeNoAck(QUEUE_NAME)
                .map(messageConverter::extractReactiveObject);

        parallelFlux
                .flatMap(starService::getWebClientStars)
                .map(personDto -> {
                    personDto.setAge(15);
                    return personDto;
                })
                .flatMap(personService::addPerson)
                .subscribe(personDto -> System.out.println("Person :: " + personDto));


//        rSocketRequester
//                .route("person.stars")
//                .data(parallelFlux)
//                .retrieveFlux(PersonDto.class)
//                .map(personDto -> {
//                    personDto.setAge(15);
//                    return personDto;
//                })
//                .doOnNext(personService::addPerson)
//                .subscribe(personDto -> System.out.println("Person :: " + personDto));
    }
}
