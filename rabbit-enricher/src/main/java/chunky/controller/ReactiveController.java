package chunky.controller;


import chunky.model.PersonDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Controller
public class ReactiveController {

    @MessageMapping("person.stars")
    public Mono<PersonDto> star(Mono<PersonDto> name){
        return name
                .map(this::tokenize)
                .delayElement(Duration.ofSeconds((long) (Math.random() * 2L)))
                .doOnNext(personDto -> log.info("rsocket request :: " + personDto.getName()));
    }

    private PersonDto tokenize(PersonDto personDto) {
        var name = personDto.getName();
        personDto.setName("***" + name + "***");
        return personDto;
    }
}
