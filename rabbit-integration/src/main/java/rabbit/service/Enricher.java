package rabbit.service;

import rabbit.repository.PersonDto;
import reactor.core.publisher.Mono;

public interface Enricher {
    Mono<PersonDto> enrich(PersonDto personDto);
}
