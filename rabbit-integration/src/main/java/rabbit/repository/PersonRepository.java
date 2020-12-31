package rabbit.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import rabbit.models.PersonDto;

public interface PersonRepository extends ReactiveMongoRepository<PersonDto, String> {
}
