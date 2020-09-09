package com.ruppyrup.rabbit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RabbitApplication {

    public static void main(String[] args) {
//        new SpringApplicationBuilder(RabbitApplication.class)
//                .web(WebApplicationType.NONE)
//                .run(args);
        SpringApplication.run(RabbitApplication.class);
    }

}
