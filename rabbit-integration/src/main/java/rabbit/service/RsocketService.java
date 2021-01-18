package rabbit.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import rabbit.models.PersonDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class RsocketService {

    @Autowired
    private RSocketRequester rSocketRequester;

    public Mono<PersonDto> rsocketEnricher(PersonDto personDto) {
        return rSocketRequester
                .route("person.stars")
                .data(Mono.just(personDto))
                .retrieveMono(PersonDto.class)
                .doOnNext(person -> System.out.println("Person enriched :: " + person.getName()));
    }
}
