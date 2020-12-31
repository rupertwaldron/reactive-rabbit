package chunky.controller;


import chunky.model.PersonDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import java.time.Duration;

@Slf4j
@Controller
public class ReactiveController {

    @MessageMapping("person.stars")
    public Flux<PersonDto> star(Flux<PersonDto> name){
        return name
                .map(this::tokenize)
                .doOnNext(personDto -> log.info("Start service request :: " + personDto.getName()))
                .delayElements(Duration.ofSeconds((long) (Math.random() * 2L)));
    }

    private PersonDto tokenize(PersonDto personDto) {
        var name = personDto.getName();
        personDto.setName("***" + name + "***");
        return personDto;
    }
}
