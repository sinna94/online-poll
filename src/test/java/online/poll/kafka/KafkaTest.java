package online.poll.kafka;

import lombok.RequiredArgsConstructor;
import online.poll.domain.poll.Poll;
import online.poll.domain.question.Question;
import online.poll.websocket.message.QuestionResult;
import online.poll.websocket.message.ResponseMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RequiredArgsConstructor
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://127.0.0.1:9092", "port=9092"})
class KafkaTest {
    static {
        System.setProperty(EmbeddedKafkaBroker.BROKER_LIST_PROPERTY, "spring.kafka.bootstrap-servers");
    }

    private final KafkaTemplate<String, KafkaMessage> kafkaTemplate;
    private final KafkaConsumer kafkaConsumer;

    @BeforeEach
    void setup() {
        kafkaConsumer.resetLatch();
    }

    @Test
    void kafkaTest() throws InterruptedException {
        Poll testPoll = Poll.builder().title("test poll").build();
        List<Question> questions = List.of(
            new Question("q1", testPoll),
            new Question("q2", testPoll),
            new Question("q3", testPoll)
        );
        KafkaMessage kafkaMessage = new KafkaMessage("/sub/poll/1",
            ResponseMessage.createResponseMessage(questions)
        );
        Thread.sleep(5000);
        kafkaTemplate.send("poll", kafkaMessage);

        boolean messageConsumed = kafkaConsumer.getLatch().await(5, TimeUnit.SECONDS);
        assertTrue(messageConsumed);

        KafkaMessage message = kafkaConsumer.getMessage();
        assertThat(message.destination()).isEqualTo(kafkaMessage.destination());

        List<QuestionResult> questionResults = message.responseMessage().getQuestionResults();
        assertThat(questionResults)
            .extracting("title")
            .containsExactly(questions.stream().map(Question::getTitle).toArray());
    }
}
