package chunky.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
public class TokenizerController {

    @GetMapping("/tokens")
    public List<String> getTokens(@RequestParam("pans") List<String> pans) throws InterruptedException {
        System.out.println("Search started");
        List<String> tokens = pans.stream()
                .map(this::tokenize)
                .peek(System.out::println)
                .collect(Collectors.toList());
        System.out.println("Search finished");
        TimeUnit.SECONDS.sleep(1L);
        return tokens;
    }

    @GetMapping("/person")
    public String getId(@RequestParam("id") String id) throws InterruptedException {
        TimeUnit.SECONDS.sleep((long) (Math.random() * 2L));
//        TimeUnit.MILLISECONDS.sleep(100L);
        System.out.println("Start service request: " + id);
        return tokenize(id);
    }

    private String tokenize(String pan) {
        return "***" + pan + "***";
    }
}
