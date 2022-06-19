package online.poll.sevice;

import lombok.RequiredArgsConstructor;
import online.poll.domain.poll.Poll;
import online.poll.domain.poll.PollRepository;
import online.poll.domain.question.Question;
import online.poll.domain.question.QuestionRepository;
import online.poll.web.dto.PollSaveRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    @Transactional
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
    }
}
