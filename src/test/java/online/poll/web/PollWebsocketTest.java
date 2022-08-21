package online.poll.web;

import lombok.RequiredArgsConstructor;
import online.poll.domain.poll.Poll;
import online.poll.domain.poll.PollRepository;
import online.poll.domain.question.Question;
import online.poll.domain.question.QuestionRepository;
import online.poll.websocket.message.PollType;
import online.poll.websocket.message.RequestMessage;
import online.poll.websocket.message.ResponseMessage;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://127.0.0.1:9092", "port=9092"})
class PollWebsocketTest {
    static {
        System.setProperty(EmbeddedKafkaBroker.BROKER_LIST_PROPERTY, "spring.kafka.bootstrap-servers");
    }
    private final PollRepository pollRepository;
    private final QuestionRepository questionRepository;
    @LocalServerPort
    private int port;

    private final BlockingQueue<ResponseMessage> queue = new LinkedBlockingQueue<>();

    @Test
    void webSocketTest() throws ExecutionException, InterruptedException, TimeoutException {
        Poll testPoll = Poll.builder().title("testPoll").timeLimit(LocalDateTime.now().plusHours(1)).build();
        List<Question> questions = List.of(new Question("q1", testPoll), new Question("q2", testPoll), new Question("q3", testPoll));
        pollRepository.save(testPoll);
        testPoll.addQuestions(questions);
        questionRepository.saveAll(questions);

        Long testPollId = testPoll.getId();
        Long questionId = testPoll.getQuestionList().get(0).getId();

        RequestMessage requestMessage = RequestMessage.builder()
            .pollId(testPollId)
            .pollType(PollType.PLUS)
            .questionId(questionId)
            .build();

        WebSocketStompClient webSocketClient = getWebSocketClient();
        webSocketClient.setMessageConverter(new MappingJackson2MessageConverter());
        MessageFrameHandler<ResponseMessage> handler = new MessageFrameHandler<>(ResponseMessage.class, queue);
        StompSession session1 = getStompSession(webSocketClient);
        StompSession session2 = getStompSession(webSocketClient);
        StompSession anotherSession = getStompSession(webSocketClient);

        session1.subscribe("/sub/poll/" + testPollId, handler);
        session2.subscribe("/sub/poll/" + testPollId, handler);
        anotherSession.subscribe("/sub/poll/" + 999, handler);

        session1.send("/pub/poll", requestMessage);

        ResponseMessage responseMessage1 = queue.poll(1, TimeUnit.SECONDS);
        ResponseMessage responseMessage2 = queue.poll(1, TimeUnit.SECONDS);
        ResponseMessage responseMessage3 = queue.poll(1, TimeUnit.SECONDS);

        assertThat(responseMessage1)
            .isNotNull();
        assertThat(responseMessage1.getQuestionResults())
            .extracting("title", "voteCount")
            .containsExactly(Tuple.tuple("q1", 1L), Tuple.tuple("q2", 0L), Tuple.tuple("q3", 0L));
        assertThat(responseMessage2)
            .isNotNull();
        assertThat(responseMessage2.getQuestionResults())
            .extracting("title", "voteCount")
            .containsExactly(Tuple.tuple("q1", 1L), Tuple.tuple("q2", 0L), Tuple.tuple("q3", 0L));
        assertThat(responseMessage3)
            .isNull();
    }

    private WebSocketStompClient getWebSocketClient() {
        StandardWebSocketClient standardWebSocketClient = new StandardWebSocketClient();
        WebSocketTransport webSocketTransport = new WebSocketTransport(standardWebSocketClient);
        List<Transport> transports = Collections.singletonList(webSocketTransport);
        SockJsClient sockJsClient = new SockJsClient(transports);

        return new WebSocketStompClient(sockJsClient);
    }

    private StompSession getStompSession(WebSocketStompClient webSocketStompClient) throws InterruptedException, ExecutionException, TimeoutException {
        return webSocketStompClient.connect("ws://localhost:" + port + "/ws", new StompSessionHandlerAdapter() {
        }).get(3, TimeUnit.SECONDS);
    }
}
