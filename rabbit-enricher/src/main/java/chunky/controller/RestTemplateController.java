package chunky.controller;

import chunky.model.PersonDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class RestTemplateController {

    @PostMapping("/slowstars")
    public PersonDto star(@RequestBody PersonDto personDto) throws InterruptedException {
        TimeUnit.SECONDS.sleep((long) (Math.random() * 2L));
        log.info("rest request :: " + personDto.getName());
        return tokenize(personDto);
    }

    private PersonDto tokenize(PersonDto personDto) {
        var name = personDto.getName();
        personDto.setName("***" + name + "***");
        return personDto;
    }

}
