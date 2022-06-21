package online.poll.sevice;

import lombok.RequiredArgsConstructor;
import online.poll.domain.poll.Poll;
import online.poll.domain.poll.PollRepository;
import online.poll.domain.question.Question;
import online.poll.domain.question.QuestionRepository;
import online.poll.web.dto.PollResponseDto;
import online.poll.web.dto.PollSaveRequestDto;
import online.poll.web.exception.PollNotExistException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class PollService {
    private final PollRepository pollRepository;
    private final QuestionRepository questionRepository;

    public Long createPoll(PollSaveRequestDto requestDto) {
        Poll poll = requestDto.toPollEntity();
        pollRepository.save(poll);

        List<Question> questionList = requestDto.toQuestionEntityList(poll)
            .stream()
            .map(questionRepository::save)
            .toList();
        poll.addQuestions(questionList);

        return poll.getId();
    }

    public PollResponseDto getPoll(Long id) {
        Poll poll = pollRepository.findById(id).orElseThrow(() -> {
            throw new PollNotExistException();
        });

        return PollResponseDto.createPollResponseDto(poll);
    }

    public Page<PollResponseDto> getPolls(Pageable pageable) {
        return pollRepository.findAll(pageable)
            .map(PollResponseDto::createPollResponseDto);
    }
}
