package com.ruppyrup.rabbit.repository;

import com.ruppyrup.rabbit.models.Person;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class PeopleRepository {
    private List<Person> people = new ArrayList<>();

    public synchronized void add(Person person) {
        people.add(person);
    }

    public List<Person> getAll() {
        return people;
    }

    public void clearPeople() {
        people.clear();
    }

}
