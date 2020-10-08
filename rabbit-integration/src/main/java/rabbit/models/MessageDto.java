package rabbit.models;

import lombok.Data;
import org.springframework.messaging.MessageHeaders;

@Data
public class MessageDto {
    private final MessageHeaders headers;
    private final PersonDto personDTO;

    public MessageDto(MessageHeaders headers, PersonDto personDTO) {
        this.headers = headers;
        this.personDTO = personDTO;
    }
}
