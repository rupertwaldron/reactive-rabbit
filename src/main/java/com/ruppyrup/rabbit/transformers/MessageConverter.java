package com.ruppyrup.rabbit.transformers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruppyrup.rabbit.models.MessageDto;
import com.ruppyrup.rabbit.models.Person;
import com.ruppyrup.rabbit.service.StarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class MessageConverter {

    @Autowired
    StarService starService;

    @ServiceActivator
    public MessageDto wrapHeaders(Message<byte[]> event) {
        return new MessageDto(event.getHeaders(), event.getPayload());
    }

    @ServiceActivator
    public Person extractObject(Message<byte[]> event) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String payload = new String(event.getPayload());
        Person result = objectMapper.readValue(payload, Person.class);
        return result;
    }

    @ServiceActivator
    public Person changeAge(Message<Person> message) throws IOException {
        message.getPayload().setAge(15);
        return message.getPayload();
    }

    @ServiceActivator
    public Person enrichObject(Message<Person> message) {
        Person person = message.getPayload();

        String s = starService.getStars(person.getName());
        person.setName(s);
        return person;
    }

}
