package online.poll.web.dto;

import online.poll.websocket.message.ResponseMessage;

public record KafkaMessage(String destination, ResponseMessage responseMessage) {
}
