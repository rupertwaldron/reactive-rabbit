package rabbit.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import rabbit.models.PersonDto;
import reactor.core.publisher.Mono;

@Service
public class StarService {

    private final WebClient webClient;

    public StarService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://localhost:8088")
                .build();
    }

    public Mono<PersonDto> getWebClientStars(PersonDto personDto) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stars")
                        .queryParam("id", personDto.getName())
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(id -> {
                    personDto.setName(id);
                    return personDto;
                });
    }
}
