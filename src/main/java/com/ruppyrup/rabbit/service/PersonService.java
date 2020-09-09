package com.ruppyrup.rabbit.service;

import com.ruppyrup.rabbit.models.Person;
import com.ruppyrup.rabbit.publisher.Publisher;
import com.ruppyrup.rabbit.repository.PeopleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class PersonService {
    public static Instant start;
    public static Instant finish;

    @Autowired
    PeopleRepository peopleRepository;

    public void addPerson(Person person) {
        if (peopleRepository.getAll().size() == 0) start = Instant.now();
        if (peopleRepository.getAll().size() == Publisher.END_INCLUSIVE - 1) finish = Instant.now();
        peopleRepository.add(person);
    }

    public List<Person> getPeople() {
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
