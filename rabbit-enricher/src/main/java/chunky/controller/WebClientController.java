package chunky.controller;

import chunky.model.PersonDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class WebClientController {

    @PostMapping("/stars")
    public Mono<PersonDto> star(@RequestBody PersonDto personDto) throws InterruptedException {
        TimeUnit.SECONDS.sleep((long) (Math.random() * 2L));
        log.info("http request :: " + personDto.getName());
        return Mono.just(tokenize(personDto));
    }

    private PersonDto tokenize(PersonDto personDto) {
        var name = personDto.getName();
        personDto.setName("***" + name + "***");
        return personDto;
    }

}
