package rabbit.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import rabbit.repository.PersonDto;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@Profile("rsocket")
public class RsocketService implements Enricher {

    private final RSocketRequester rSocketRequester;

    public RsocketService(RSocketRequester rSocketRequester) {
        this.rSocketRequester = rSocketRequester;
    }

    public Mono<PersonDto> enrich(PersonDto personDto) {
        return rSocketRequester
                .route("person.stars")
                .data(Mono.just(personDto))
                .retrieveMono(PersonDto.class);
    }
}
