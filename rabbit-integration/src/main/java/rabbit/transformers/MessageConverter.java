package rabbit.transformers;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Delivery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rabbit.models.PersonDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Service
public class MessageConverter {

    public boolean checkForEOD(Delivery delivery) {
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
        Object sendObject = Optional.ofNullable(delivery.getProperties().getHeaders().get("sendTime")).orElseThrow();
        String sendTime = sendObject.toString();
        ObjectMapper objectMapper = new ObjectMapper();
        PersonDto person = null;
        try {
            person = objectMapper.readValue(delivery.getBody(), PersonDto.class);
            person.setCreation(sendTime);
        } catch (IOException ex) {
            log.error("Can't convert object to PersonDto :: " + ex.getMessage());
        }
        return person;
    }
}
