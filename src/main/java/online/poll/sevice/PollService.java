package online.poll.sevice;

import lombok.RequiredArgsConstructor;
import online.poll.domain.poll.Poll;
import online.poll.domain.poll.PollRepository;
import online.poll.domain.question.Question;
import online.poll.domain.question.QuestionRepository;
import online.poll.web.dto.PollSaveRequestDto;
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
}
