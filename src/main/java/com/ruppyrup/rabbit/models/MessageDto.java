package com.ruppyrup.rabbit.models;

import lombok.Data;
import lombok.Getter;
import org.springframework.messaging.MessageHeaders;

@Data
public class MessageDto {
    private final MessageHeaders headers;
    private final Object payload;

    public MessageDto(MessageHeaders headers, Object payload) {
        this.headers = headers;
        this.payload = payload;
    }
}
