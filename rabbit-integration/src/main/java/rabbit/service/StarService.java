package rabbit.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import rabbit.models.PersonDto;
import reactor.core.publisher.Flux;

@Service
public class StarService {

    private final WebClient webClient;

    public StarService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://localhost:8089")
                .build();
    }

    private RestTemplate restTemplate = new RestTemplate();

    public String getStars(String id) {
        UriComponentsBuilder uriComponents = UriComponentsBuilder
                .fromHttpUrl("http://localhost:8088")
                .path("person")
                .queryParam("id", id);
        ResponseEntity<String> response = restTemplate.getForEntity(uriComponents.toUriString(), String.class);
        return response.getBody();
    }

    public Flux<PersonDto> getWebClientStars(Flux<PersonDto> personDtoFlux) {
        return webClient.get()
                .uri("/stars")
                .retrieve()
                .bodyToFlux(PersonDto.class);

    }
}
