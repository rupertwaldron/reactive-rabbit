package rabbit;

import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import rabbit.models.PersonDto;
import rabbit.service.PersonService;
import rabbit.service.RsocketService;
import rabbit.service.StarService;
import rabbit.transformers.MessageConverter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
@SpringBootApplication
@EnableAsync
public class RabbitApplication implements CommandLineRunner {

    private static final String QUEUE_NAME = "myqueue2";

    private final RsocketService rsocketService;

    private final PersonService personService;

    private final MessageConverter messageConverter;

    private final StarService starService;

    public RabbitApplication(RsocketService rsocketService,
                             PersonService personService,
                             MessageConverter messageConverter, StarService starService) {
        this.rsocketService = rsocketService;
        this.personService = personService;
        this.messageConverter = messageConverter;
        this.starService = starService;
    }

    public static void main(String[] args) {
        SpringApplication.run(RabbitApplication.class);
    }

    @Override
    public void run(String... args) {

        Path path = Paths.get("person_out.txt");

        ReceiverOptions receiverOptions = getReceiverOptions();

        try (Receiver receiver = RabbitFlux.createReceiver(receiverOptions)) {
            receiver
                    .consumeManualAck(QUEUE_NAME)
                    .flatMap(delivery -> {
                        if (messageConverter.checkForEOD(delivery)) {
                            return processEndOfDay(path, delivery);
                        } else {
                            return processPersonMessages(delivery);
                        }
                    })
                    .subscribe(message -> log.info("Finished if else :: " + message));
        } catch (Exception ex) {
            log.error(ex.getLocalizedMessage());
        }
    }

    private ReceiverOptions getReceiverOptions() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.useNio();
        factory.setHost("localhost");

        return new ReceiverOptions()
                .connectionFactory(factory)
                .connectionSubscriptionScheduler(Schedulers.boundedElastic());
    }

    private Flux<PersonDto> processPersonMessages(AcknowledgableDelivery delivery) {
        return Flux.just(delivery)
                .map(messageConverter::extractReactiveObject)
                .onErrorContinue(((throwable, o) -> {
                    log.error("Processing error on object ::" + o);
                    delivery.nack(false);
                }))
                .flatMap(rsocketService::rsocketEnricher)
                .onErrorContinue(((throwable, o) -> {
                    log.error("Enrichment error error on object ::" + o);
                    delivery.nack(false);
                }))
                .map(personDto -> {
                    personDto.setAge(15);
                    return personDto;
                })
                .flatMap(personService::addPerson)
                .doOnNext(personDto -> delivery.ack());


//                .flatMap(starService::getWebClientStars)
//                .onErrorContinue(((throwable, o) -> {
//                    log.error("Enrichment error error on object ::" + o);
//                    delivery.nack(false);
//                }))
//                .map(personDto -> {
//                    personDto.setAge(15);
//                    return personDto;
//                })
//                .flatMap(personService::addPerson)
//                .doOnNext(personDto -> delivery.ack());
    }

//    private Flux<PersonDto> processPersonMessages(AcknowledgableDelivery delivery) {
//        return Flux.just(delivery)
//                .map(messageConverter::extractReactiveObject)
//                .onErrorContinue(((throwable, o) -> {
//                    log.error("Processing error on object ::" + o);
//                    delivery.nack(false);
//                }))
//                .flatMap(starService::getWebClientStars)
//                .onErrorContinue(((throwable, o) -> {
//                    log.error("Enrichment error error on object ::" + o);
//                    delivery.nack(false);
//                }))
//                .map(personDto -> {
//                    personDto.setAge(15);
//                    return personDto;
//                })
//                .flatMap(personService::addPerson)
//                .doOnNext(personDto -> delivery.ack());
//    }

    private Flux<Void> processEndOfDay(Path path, AcknowledgableDelivery delivery) {
        return Flux.just(delivery)
                .map(messageConverter::getEODTime)
                .flatMap(personService::collectEODPeople)
                .doOnNext(person -> {
                    try {
                        Files.writeString(path, person.toString() + "\n", StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .map(PersonDto::getId)
                .doOnNext(id -> delivery.ack())
                .flatMap(personService::deleteById);
    }
}
