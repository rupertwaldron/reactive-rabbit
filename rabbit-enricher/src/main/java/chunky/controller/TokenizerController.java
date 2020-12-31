package chunky.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class TokenizerController {

    @GetMapping("/person")
    public String getId(@RequestParam("id") String id) throws InterruptedException {
        TimeUnit.SECONDS.sleep((long) (Math.random() * 2L));
        log.info("Start service request: " + id);
        return tokenize(id);
    }

    private String tokenize(String pan) {
        return "***" + pan + "***";
    }
}
