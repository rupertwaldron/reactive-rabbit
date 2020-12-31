package rabbit;

import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import rabbit.models.PersonDto;
import rabbit.service.PersonService;
import rabbit.service.StarService;
import rabbit.transformers.MessageConverter;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.Receiver;
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

        ConnectionFactory factory = new ConnectionFactory();
        factory.useNio();
        factory.setHost("localhost");

        ReceiverOptions receiverOptions = new ReceiverOptions()
                .connectionFactory(factory)
                .connectionSubscriptionScheduler(Schedulers.boundedElastic());

        try (Receiver receiver = RabbitFlux.createReceiver(receiverOptions)) {
            receiver
                    .consumeAutoAck(QUEUE_NAME)
                    .flatMap(delivery -> {
                        if (messageConverter.checkForEOD(delivery)) {
                            return Mono.just(delivery)
                                    .map(messageConverter::getEODTime)
                                    .flatMapMany(personService::collectEODPeople)
                                    .log("EOD route")
                                    .map(PersonDto::getId)
                                    .flatMap(personService::deleteById);
                        } else {
                            return Mono.just(delivery)
                                    .map(messageConverter::extractReactiveObject)
                                    .flatMapMany(starService::getWebClientStars)
                                    .log("Normal route")
                                    .map(personDto -> {
                                        personDto.setAge(15);

                                        return personDto;
                                    })
                                    .flatMap(personService::addPerson);
                        }
                    })
                    .subscribe(message -> log.info("Finished if else :: " + message));
        } catch (Exception ex) {
            log.error(ex.getLocalizedMessage());
        }
    }
}
