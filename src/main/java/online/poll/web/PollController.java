package online.poll.web;

import lombok.RequiredArgsConstructor;
import online.poll.sevice.PollService;
import online.poll.web.dto.PollResponseDto;
import online.poll.web.dto.PollSaveRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/poll")
public class PollController {

    private final PollService pollService;

    @PostMapping
    public Long createPoll(@RequestBody PollSaveRequestDto requestDto) {
        return pollService.createPoll(requestDto);
    }

    @GetMapping
    public Page<PollResponseDto> getPolls(Pageable pageable) {
        return pollService.getPolls(pageable);
    }

    @GetMapping("{id}")
    public PollResponseDto getPoll(@PathVariable Long id) {
        return pollService.getPoll(id);
    }
}
