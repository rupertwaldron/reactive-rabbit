package rabbit.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import rabbit.models.PersonDto;
import rabbit.service.PersonService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/people")
    public Flux<PersonDto> getPeople() {
        return personService.getPeople();
    }

    @GetMapping("/info")
    public String getTime() {
        return "Time lapsed = " + personService.getTime() + "\nCount = " + personService.getCount();
    }

    @GetMapping("/clear")
    public Mono<Void> clearAllPeople() {
        return personService.clearAll();
    }

    @GetMapping("/delete/{id}")
    public Mono<Void> deletePeople(@PathVariable String id) {
        return personService.deleteById(id);
    }
}
