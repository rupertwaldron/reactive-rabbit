package rabbit;

import org.reactivestreams.Publisher;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
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
import reactor.core.publisher.Flux;

@SpringBootApplication
@EnableAsync
public class RabbitApplication implements CommandLineRunner {

    @Autowired
    StarService starService;

    @Autowired
    PersonService personService;

    @Autowired
    private RSocketRequester rSocketRequester;

    @Autowired
    @Qualifier("amqpInbound")
    public Publisher<Message<PersonDto>> amqpPublisher;

    public static void main(String[] args) {
//        new SpringApplicationBuilder(RabbitApplication.class)
//                .web(WebApplicationType.NONE)
//                .run(args);
        SpringApplication.run(RabbitApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {
        Flux<PersonDto> personDtoFlux = Flux.from(amqpPublisher)
                .map(Message::getPayload);

        starService.getWebClientStars(personDtoFlux)
                .map(personDto -> {
                    personDto.setAge(15);
                    return personDto;
                })
                .doOnNext(personService::addPerson)
                .subscribe(personDto -> System.out.println("Person :: " + personDto));

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
