package rabbit.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import rabbit.repository.PersonDto;
import reactor.core.publisher.Mono;

@Service
@Profile("webclient")
public class WebClientService implements Enricher{

    private final WebClient webClient;

    public WebClientService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://localhost:8088")
                .build();
    }

    public Mono<PersonDto> enrich(PersonDto personDto) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/stars")
                        .build())
                .body(BodyInserters.fromValue(personDto))
                .retrieve()
                .bodyToMono(PersonDto.class);
    }
}
