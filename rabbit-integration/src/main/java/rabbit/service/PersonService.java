package rabbit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import rabbit.models.PersonDto;
import rabbit.repository.PeopleRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class PersonService {
    private final Instant start = Instant.now();
    private Instant finish;

    @Autowired
    PeopleRepository peopleRepository;

    public void addPerson(PersonDto personDTO) {
        peopleRepository.add(personDTO);
        finish = Instant.now();
    }

    public List<PersonDto> getPeople() {
        return peopleRepository.getAll();
    }

    public long getTime() {
        return Duration.between(start, finish).toMillis();
    }

    public int getCount() {
        return peopleRepository.getAll().size();
    }

    public void clearAll() {
        peopleRepository.clearPeople();
    }
}
