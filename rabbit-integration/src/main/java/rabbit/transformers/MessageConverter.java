package rabbit.transformers;


import com.fasterxml.jackson.databind.ObjectMapper;

import com.rabbitmq.client.Delivery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import rabbit.models.MessageDto;
import rabbit.models.PersonDto;
import rabbit.service.StarService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class MessageConverter {

    @Autowired
    StarService starService;

    public MessageDto wrapHeaders(Message<byte[]> event) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        PersonDto result = objectMapper.readValue(event.getPayload(), PersonDto.class);
        return new MessageDto(event.getHeaders(), result);
    }

    public PersonDto extractObject(Message<byte[]> event) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String payload = new String(event.getPayload());
        return objectMapper.readValue(payload, PersonDto.class);
    }

    public boolean checkForEOD(Delivery delivery) {
        System.out.println("In EOD " + delivery.getProperties().getHeaders());
        Object endOfDayTime = delivery.getProperties().getHeaders().get("endOfDayTime");
        return endOfDayTime != null;
    }

    public LocalDateTime getEODTime(Delivery delivery) {
        final DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        String endOfDayTime = delivery.getProperties().getHeaders().get("endOfDayTime").toString();
        return LocalDateTime.parse(endOfDayTime, formatter);
    }

    public PersonDto extractReactiveObject(Delivery delivery) {
        String sendTime = delivery.getProperties().getHeaders().get("sendTime").toString();
        ObjectMapper objectMapper = new ObjectMapper();
        PersonDto person = null;
        try {
            person = objectMapper.readValue(delivery.getBody(), PersonDto.class);
            person.setCreation(sendTime);
        } catch (IOException ex) {
            System.out.println("Can't convert object to PersonDto");
        }
        return person;
    }

    public PersonDto extractPerson(MessageDto messageDto) {
        PersonDto personDTO = messageDto.getPersonDTO();
        String creationTime = (String) messageDto.getHeaders().get("sendTime");
        personDTO.setCreation(creationTime);
        return messageDto.getPersonDTO();
    }

    public PersonDto changeAge(Message<PersonDto> message) {
        message.getPayload().setAge(15);
        return message.getPayload();
    }

    public PersonDto enrichObject(Message<PersonDto> message) {
        PersonDto personDTO = message.getPayload();

        String s = starService.getStars(personDTO.getName());
        personDTO.setName(s);
        return personDTO;
    }

}
