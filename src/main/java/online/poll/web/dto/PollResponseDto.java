package online.poll.web.dto;

import lombok.Getter;
import online.poll.domain.poll.Poll;
import online.poll.domain.question.Question;

import java.time.LocalDateTime;
import java.util.List;

public record PollResponseDto(
    String title,
    LocalDateTime timeLimit,
    boolean completed,
    List<Question> questionList
) {
    public static PollResponseDto createPollResponseDto(Poll poll) {
        return new PollResponseDto(
            poll.getTitle(),
            poll.getTimeLimit(),
            poll.isCompleted(),
            poll.getQuestionList()
        );
    }
}
