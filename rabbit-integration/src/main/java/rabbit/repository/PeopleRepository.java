package rabbit.repository;


import org.springframework.stereotype.Repository;
import rabbit.models.PersonDto;


import java.util.ArrayList;
import java.util.List;

@Repository
public class PeopleRepository {
    private List<PersonDto> people = new ArrayList<>();

    public synchronized void add(PersonDto personDTO) {
        people.add(personDTO);
    }

    public List<PersonDto> getAll() {
        return people;
    }

    public void clearPeople() {
        people.clear();
    }

}
