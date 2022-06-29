package online.poll.web;

import lombok.RequiredArgsConstructor;
import online.poll.domain.poll.Poll;
import online.poll.sevice.PollService;
import online.poll.web.dto.PollResponseDto;
import online.poll.web.dto.PollSaveRequestDto;
import online.poll.web.exception.PollNotExistException;
import online.poll.websocket.message.RequestMessage;
import online.poll.websocket.message.ResponseMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import reactor.core.publisher.Mono;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PollControllerTest {

    private final WebTestClient webTestClient;
    @MockBean
    PollService mockPollService;

    @Test
    void createPoll() {
        when(mockPollService.createPoll(any()))
            .thenReturn(1L);

        PollSaveRequestDto requestDto = PollSaveRequestDto.builder().title("title").questionTitles(List.of("q1", "q1")).build();
        webTestClient.post()
            .uri("/api/v1/poll")
            .body(Mono.just(requestDto), PollSaveRequestDto.class).exchange()
            .expectStatus().isOk()
            .expectBody(Long.class).isEqualTo(1L);
    }

    @Test
    void getPoll() {
        PollResponseDto responseDto = new PollResponseDto(1L, "title", null, false, List.of());
        when(mockPollService.getPoll(anyLong()))
            .thenReturn(responseDto);

        webTestClient.get()
            .uri("/api/v1/poll/1")
            .exchange()
            .expectStatus().isOk()
            .expectBody(PollResponseDto.class)
            .isEqualTo(responseDto);
    }

    @DisplayName("존재하지 않는 id 로 조회 시도")
    @Test
    void getPollNotExist() {
        when(mockPollService.getPoll(anyLong()))
            .thenThrow(new PollNotExistException());

        webTestClient.get()
            .uri("/api/v1/poll/1")
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    void getPolls() {
        List<PollResponseDto> pollList = IntStream.rangeClosed(7, 10)
            .mapToObj(num -> Poll.builder().title("t" + num).build())
            .map(PollResponseDto::createPollResponseDto)
            .toList();
        PageRequest pageRequest = PageRequest.of(1, 6);

        Page<PollResponseDto> page = new PageImpl<>(pollList, pageRequest, 10);
        when(mockPollService.getPolls(any()))
            .thenReturn(page);

        webTestClient.get()
            .uri(uriBuilder -> uriBuilder.path("/api/v1/poll")
                .queryParam("page", "1")
                .queryParam("size", "6")
                .build())
            .exchange()
            .expectStatus().isOk();
    }
}