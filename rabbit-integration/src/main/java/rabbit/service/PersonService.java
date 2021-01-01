package rabbit.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rabbit.models.PersonDto;
import rabbit.repository.PersonRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
public class PersonService {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
    
    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public Mono<PersonDto> addPerson(PersonDto personDTO) {
        personDTO.setSavation(LocalDateTime.now().format(formatter));
        return personRepository.save(personDTO);
    }
    public Flux<PersonDto> getPeople() {
        return personRepository.findAll();
    }

    public Long getTime() {
        Flux<PersonDto> lastPerson = personRepository
                .findAll(Sort.by("savation"))
                .takeLast(1);

        Flux<Long> map = lastPerson
                .map(personDto -> {
                    LocalDateTime creation = LocalDateTime.parse(personDto.getCreation(), formatter);
                    LocalDateTime savation = LocalDateTime.parse(personDto.getSavation(), formatter);
                    return ChronoUnit.MILLIS.between(creation, savation);
                });

        return map.blockLast();
    }

    public Flux<PersonDto> collectEODPeople(LocalDateTime eod) {
        return getPeople()
                .filter(personDto -> LocalDateTime.parse(personDto.getCreation(), formatter).isBefore(eod));
    }

    public Long getCount() {
        return personRepository.findAll().count().block();
    }

    public Mono<Void> clearAll() {
        return personRepository.deleteAll();
    }

    public Mono<Void> deleteById(String id) {
//        log.info("Person to be deleted with id :: " + id);
        return personRepository.deleteById(id);
    }
}
