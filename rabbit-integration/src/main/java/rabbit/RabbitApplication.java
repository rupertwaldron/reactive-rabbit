package rabbit;

import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import rabbit.models.PersonDto;
import rabbit.service.PersonService;
import rabbit.service.StarService;
import rabbit.transformers.MessageConverter;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.ReceiverOptions;

@Slf4j
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

        personService.deleteById("5fecd3094ec4ba046ad85d4e");

        ConnectionFactory factory = new ConnectionFactory();
        factory.useNio();
        factory.setHost("localhost");

        ReceiverOptions receiverOptions = new ReceiverOptions()
                .connectionFactory(factory)
                .connectionSubscriptionScheduler(Schedulers.boundedElastic());

        ConnectableFlux<Delivery> rabbitFlux = RabbitFlux.createReceiver(receiverOptions)
                .consumeNoAck(QUEUE_NAME)
                .publish();

        rabbitFlux
                .filter(messageConverter::checkForEOD)
                .map(messageConverter::getEODTime)
                .flatMap(personService::collectEODPeople)
                .map(PersonDto::getId)
                .doOnNext(personService::deleteById)
                .subscribe(message -> log.info("People before EOD Deleted"));

        rabbitFlux
                .filter(delivery -> !messageConverter.checkForEOD(delivery))
                .map(messageConverter::extractReactiveObject)
                .flatMap(starService::getWebClientStars)
                .map(personDto -> {
                    personDto.setAge(15);

                    return personDto;
                })
                .flatMap(personService::addPerson)
                .subscribe(personDto -> log.info("Person :: " + personDto));

        rabbitFlux.connect(); // need this else nothing works


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
