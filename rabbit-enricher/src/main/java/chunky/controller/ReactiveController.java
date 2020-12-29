package chunky.controller;


import chunky.model.PersonDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Controller
public class ReactiveController {

    @MessageMapping("person.stars")
    public Flux<PersonDto> star(Flux<PersonDto> name){
        return name
                .map(this::tokenize)
                .doOnNext(personDto -> System.out.println("Start service request :: " + personDto.getName()))
                .delayElements(Duration.ofMillis(100));
    }

    private PersonDto tokenize(PersonDto personDto) {
        try {
            TimeUnit.SECONDS.sleep((long) (Math.random() * 2L));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        var name = personDto.getName();
        personDto.setName("***" + name + "***");
        return personDto;
    }
}
