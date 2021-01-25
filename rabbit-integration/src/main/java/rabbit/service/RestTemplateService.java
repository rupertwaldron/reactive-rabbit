package rabbit.service;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import rabbit.repository.PersonDto;
import reactor.core.publisher.Mono;

@Service
@Profile("rest")
public class RestTemplateService implements Enricher {

    private final RestTemplate restTemplate = new RestTemplate();

    public Mono<PersonDto> enrich(PersonDto personDto) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl("http://localhost:8088/slowstars");

        ResponseEntity<PersonDto> entity = restTemplate
                .postForEntity(builder.toUriString(), personDto, PersonDto.class);

        return Mono.just(entity.getBody());
    }

}
