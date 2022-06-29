package online.poll.sevice;

import lombok.RequiredArgsConstructor;
import online.poll.domain.poll.Poll;
import online.poll.domain.poll.PollRepository;
import online.poll.domain.question.Question;
import online.poll.domain.question.QuestionRepository;
import online.poll.web.dto.PollResponseDto;
import online.poll.web.dto.PollSaveRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

@RequiredArgsConstructor
@SpringBootTest
class PollServiceTest {

    private final PollRepository pollRepository;
    private final QuestionRepository questionRepository;
    private final PollService pollService;

    @BeforeEach
    void setUp() {
        pollRepository.deleteAll();
        questionRepository.deleteAll();
    }

    @DisplayName("투표, 질문 생성 테스트")
    @Test
    @Transactional(readOnly = true)
    void createPoll() {
        PollSaveRequestDto requestDto = PollSaveRequestDto.builder()
            .title("test poll")
            .questionTitles(List.of("Q1", "Q2", "Q3"))
            .build();

        Long pollId = pollService.createPoll(requestDto);
        Poll poll = pollRepository.findById(pollId).orElseThrow();

        assertThat(poll)
            .isNotNull()
            .extracting("title")
            .isEqualTo("test poll");
        List<Question> questionList = poll.getQuestionList();
        assertThat(questionList)
            .hasSize(3)
            .extracting("title")
            .contains("Q1", "Q2", "Q3");
        assertThat(questionList)
            .extracting("id")
            .doesNotContainNull();
    }

    @DisplayName("투표 전체 페이징 조회")
    @Test
    @Transactional(readOnly = true)
    void getPolls() {
        pollRepository.saveAll(
            IntStream.rangeClosed(1, 10)
                .mapToObj(num -> Poll.builder().title("t" + num).build())
                .toList()
        );

        PageRequest pageRequest = PageRequest.of(1, 6);
        Page<PollResponseDto> polls = pollService.getPolls(pageRequest);
        assertThat(polls.getTotalElements())
            .isEqualTo(10L);
        assertThat(polls.getContent())
            .hasSize(4)
            .extracting("title")
            .contains("t7", "t8", "t9", "t10");
    }

    @DisplayName("id로 투표 조회")
    @Test
    @Transactional(readOnly = true)
    void getPoll() {
        Poll poll = Poll.builder()
            .title("title")
            .timeLimit(LocalDateTime.now())
            .build();

        List<Question> questions = List.of(
            new Question("q1", poll),
            new Question("q2", poll),
            new Question("q3", poll)
        );

        poll.addQuestions(questions);
        pollRepository.save(poll);

        PollResponseDto pollResult = pollService.getPoll(poll.getId());
        assertThat(pollResult)
            .isNotNull()
            .extracting("title", "completed")
            .contains("title", false);
        assertThat(pollResult)
            .extracting("timeLimit")
            .isNotNull();
        assertThat(pollResult.questionList())
            .hasSize(3)
            .extracting("title")
            .contains("q1", "q2", "q3");
    }
}
