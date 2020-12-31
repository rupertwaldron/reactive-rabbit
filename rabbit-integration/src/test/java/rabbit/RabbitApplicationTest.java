package rabbit;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class RabbitApplicationTest {

    @Test
    void checkColdFlux() {
        Flux<Integer> numbers = Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        numbers
                .subscribe(number -> System.out.println("The number from 1 is ::" + number));

        numbers
                .subscribe(number -> System.out.println("The number from 2 is ::" + number));

        StepVerifier.create(numbers)
                .expectNextCount(10)
                .verifyComplete();
    }

    @Test
    void checkHotFlux() {
        ConnectableFlux<Integer> numbers = Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).publish();

        numbers
                .filter(number -> number % 2 == 0)
                .subscribe(number -> System.out.println("The number from 1 is ::" + number));

        numbers
                .filter(number -> number % 3 == 0)
                .subscribe(number -> System.out.println("The number from 2 is ::" + number));

        numbers.connect();

    }

    @Test
    void checkColdFluxFilter() {
        Flux<Integer> numbers = Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        numbers
                .filter(number -> number % 2 == 0)
                .subscribe(number -> System.out.println("The number from 1 is ::" + number));

        numbers
                .filter(number -> number % 3 == 0)
                .subscribe(number -> System.out.println("The number from 2 is ::" + number));

    }

    @Test
    void checkColdFluxTakeWhile() {
        Flux<Integer> numbers = Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        numbers
                .filter(number -> number % 2 == 0)
                .log("from 2s")
                .subscribe(number -> System.out.println("The number from 1 is ::" + number));

        numbers
                .filter(number -> number % 3 == 0)
                .log("from 3s")
                .subscribe(number -> System.out.println("The number from 2 is ::" + number));

    }

}