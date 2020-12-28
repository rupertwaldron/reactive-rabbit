package chunky.controller;

import chunky.model.PersonDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
public class WebClientController {

    @GetMapping("stars")
    public Flux<PersonDto> star(Flux<PersonDto> name){
        return name
                .map(this::tokenize)
                .doOnNext(personDto -> System.out.println("Start service request :: " + personDto.getName()))
                .delayElements(Duration.ofMillis(100));
    }

    private PersonDto tokenize(PersonDto personDto) {
        var name = personDto.getName();
        personDto.setName("***" + name + "***");
        return personDto;
    }
}
