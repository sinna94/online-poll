package online.poll.web;

import lombok.RequiredArgsConstructor;
import online.poll.sevice.PollService;
import online.poll.web.dto.PollSaveRequestDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/poll")
public class PollController {

    private final PollService pollService;

    @PostMapping
    public Long createPoll(@RequestBody PollSaveRequestDto requestDto) {
        return pollService.createPoll(requestDto);
    }
}
