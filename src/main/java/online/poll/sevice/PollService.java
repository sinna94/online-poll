package online.poll.sevice;

import lombok.RequiredArgsConstructor;
import online.poll.domain.poll.Poll;
import online.poll.domain.poll.PollRepository;
import online.poll.domain.question.Question;
import online.poll.domain.question.QuestionRepository;
import online.poll.web.dto.PollResponseDto;
import online.poll.web.dto.PollSaveRequestDto;
import online.poll.web.exception.PollNotExistException;
import online.poll.websocket.message.PollType;
import online.poll.websocket.message.RequestMessage;
import online.poll.websocket.message.ResponseMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
@Transactional
public class PollService {
    private final PollRepository pollRepository;
    private final QuestionRepository questionRepository;

    public Long createPoll(PollSaveRequestDto requestDto) {
        Poll poll = requestDto.toPollEntity();
        pollRepository.save(poll);

        List<Question> questionList = requestDto.toQuestionEntityList(poll);
        questionRepository.saveAll(questionList);
        poll.addQuestions(questionList);

        return poll.getId();
    }

    public PollResponseDto getPoll(Long id) {
        Poll poll = findById(id);

        return PollResponseDto.createPollResponseDto(poll);
    }

    private Poll findById(Long id) {
        return pollRepository.findById(id).orElseThrow(() -> {
            throw new PollNotExistException();
        });
    }

    public Page<PollResponseDto> getPolls(Pageable pageable) {
        return pollRepository.findAll(pageable)
            .map(PollResponseDto::createPollResponseDto);
    }

    public ResponseMessage poll(RequestMessage message) {
        Long pollId = message.pollId();
        Poll poll = findById(pollId);

        var isFinishedPoll = isFinishedPoll(poll);
        setPollToCompeted(poll, isFinishedPoll);

        if (isFinishedPoll) {
            return ResponseMessage.createResponseMessage(poll.getQuestionList());
        }

        Long questionId = message.questionId();
        PollType pollType = message.pollType();

        Question question = getQuestion(poll, questionId);

        switch (pollType) {
            case PLUS -> {
                question.increaseVoteCount();
            }
            case MINUS -> {
                question.decreaseVoteCount();
            }
            default -> throw new IllegalStateException("Unexpected pollType: " + pollType);
        }

        return ResponseMessage.createResponseMessage(poll.getQuestionList());
    }

    private void setPollToCompeted(Poll poll, boolean isFinishedPoll) {
        if (!poll.isCompleted() && isFinishedPoll) {
            poll.setCompleted();
        }
    }

    private boolean isFinishedPoll(Poll poll) {
        return poll.isCompleted() || poll.getTimeLimit().isBefore(LocalDateTime.now());
    }

    private Question getQuestion(Poll poll, Long questionId) {
        return poll.getQuestionList().stream()
            .filter(q -> q.getId().equals(questionId))
            .findFirst().orElseThrow(() -> new IllegalArgumentException("Invalid questionId : " + questionId));
    }
}
