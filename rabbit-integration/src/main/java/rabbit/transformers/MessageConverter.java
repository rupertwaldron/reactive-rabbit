package rabbit.transformers;


import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import rabbit.models.MessageDto;
import rabbit.models.PersonDto;
import rabbit.service.StarService;

import java.io.IOException;

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
