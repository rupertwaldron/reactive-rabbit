package rabbit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import rabbit.models.Person;
import rabbit.service.PersonService;

import java.util.List;

@RestController
public class PersonController {

    @Autowired
    PersonService personService;

    @GetMapping("/people")
    public List<Person> getPeople() {
        return personService.getPeople();
    }

    @GetMapping("/info")
    public String getTime() {
        return "Time lapsed = " + personService.getTime() + "\nCount = " + personService.getCount();
    }

    @GetMapping("/clear")
    public void clearAllPeople() {
        personService.clearAll();
    }
}
