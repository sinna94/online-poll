package online.poll.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

@Component
public class KafkaConsumer {
    private CountDownLatch latch = new CountDownLatch(1);
    private KafkaMessage message;

    @KafkaListener(topics = "poll", groupId = "poll")
    public void receive(KafkaMessage message) {
        this.message = message;
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void resetLatch(){
        latch = new CountDownLatch(1);
    }

    public KafkaMessage getMessage() {
        return message;
    }
}
