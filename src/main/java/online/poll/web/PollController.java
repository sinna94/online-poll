package online.poll.web;

import lombok.RequiredArgsConstructor;
import online.poll.sevice.PollService;
import online.poll.web.dto.PollResponseDto;
import online.poll.web.dto.PollSaveRequestDto;
import online.poll.websocket.message.RequestMessage;
import online.poll.websocket.message.ResponseMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PollController {

    private final PollService pollService;
    private final SimpMessageSendingOperations simpMessageSendingOperations;

    @PostMapping("api/v1/poll")
    public Long createPoll(@RequestBody PollSaveRequestDto requestDto) {
        return pollService.createPoll(requestDto);
    }

    @GetMapping("api/v1/poll")
    public Page<PollResponseDto> getPolls(Pageable pageable) {
        return pollService.getPolls(pageable);
    }

    @GetMapping("api/v1/poll/{id}")
    public PollResponseDto getPoll(@PathVariable Long id) {
        return pollService.getPoll(id);
    }

    @MessageMapping("/poll")
    public void message(RequestMessage message){
        ResponseMessage responseMessage = pollService.poll(message);

        Long pollId = message.pollId();
        String destination = "/sub/poll/" + pollId;
        simpMessageSendingOperations.convertAndSend(destination, responseMessage);
    }
}
