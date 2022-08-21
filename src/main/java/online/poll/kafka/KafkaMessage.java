package online.poll.kafka;

import online.poll.websocket.message.ResponseMessage;

public record KafkaMessage(String destination, ResponseMessage responseMessage) {
}
