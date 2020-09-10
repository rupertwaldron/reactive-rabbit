package rabbit.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class StarService {


    private RestTemplate restTemplate = new RestTemplate();

    public String getStars(String id) {
        UriComponentsBuilder uriComponents = UriComponentsBuilder
                .fromHttpUrl("http://localhost:8088")
                .path("person")
                .queryParam("id", id);
        ResponseEntity<String> response = restTemplate.getForEntity(uriComponents.toUriString(), String.class);
        return response.getBody();
    }
}
