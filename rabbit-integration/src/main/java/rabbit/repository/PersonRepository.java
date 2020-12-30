package rabbit.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import rabbit.models.PersonDto;
import reactor.core.publisher.Flux;

public interface PersonRepository extends ReactiveMongoRepository<PersonDto, String> {
}
