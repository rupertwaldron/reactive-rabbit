package rabbit;

import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import rabbit.repository.PersonDto;
import rabbit.service.Enricher;
import rabbit.repository.PersonService;
import rabbit.service.WebClientService;
import rabbit.transformers.MessageConverter;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
@SpringBootApplication
//@EnableAsync
public class RabbitApplication implements CommandLineRunner {

    private static final String QUEUE_NAME = "myqueue2";

    private final PersonService personService;

    private final MessageConverter messageConverter;

    private final Enricher enricherService;

    public RabbitApplication(PersonService personService,
                             MessageConverter messageConverter,
                             Enricher enricherService) {
        this.personService = personService;
        this.messageConverter = messageConverter;
        this.enricherService = enricherService;
    }

    public static void main(String[] args) {
        SpringApplication.run(RabbitApplication.class);
    }

    @Override
    public void run(String... args) {

        Path path = Paths.get("person_out.txt");

        ReceiverOptions receiverOptions = getReceiverOptions();

        try (Receiver receiver = RabbitFlux.createReceiver(receiverOptions)) {
            final Flux<AcknowledgableDelivery> acknowledgableDeliveryFlux = receiver
                    .consumeManualAck(QUEUE_NAME)
                    .share();


            acknowledgableDeliveryFlux
                    .filter(messageConverter::checkForEOD)
                    .flatMap(delivery -> processEndOfDay(path, delivery))
                    .subscribe(message -> log.info("Finished EOD process :: " + message));

            acknowledgableDeliveryFlux
                    .filter(delivery -> !messageConverter.checkForEOD(delivery))
                    .flatMap(this::processPersonMessages)
                    .subscribe(message -> log.info("Finished Person process :: " + message));
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
                .flatMap(enricherService::enrich)
                .onErrorContinue(((throwable, o) -> {
                    log.error("Enrichment error error on object ::" + o);
                    delivery.nack(true);
                }))
                .map(personDto -> {
                    personDto.setAge(15);
                    return personDto;
                })
                .flatMap(personService::addPerson)
                .doOnNext(personDto -> delivery.ack());
    }


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
