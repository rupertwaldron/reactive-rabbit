package chunky.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
public class WebClientController {

    @GetMapping("/stars")
    public String star(@RequestParam("id") String id) {
        try {
            TimeUnit.SECONDS.sleep((long) (Math.random() * 2L));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Start service request :: " + id);
        return "****" + id + "****";
    }

}
