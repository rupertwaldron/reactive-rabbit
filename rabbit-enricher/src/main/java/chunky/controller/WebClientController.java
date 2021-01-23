package chunky.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class WebClientController {

    @GetMapping("/stars")
    public String star(@RequestParam("id") String id) throws InterruptedException {
        TimeUnit.SECONDS.sleep((long) (Math.random() * 2L));
        log.info("http request :: " + id);
        return "****" + id + "****";
    }

}
