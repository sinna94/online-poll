package online.poll.web.dto;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import online.poll.domain.poll.Poll;
import online.poll.domain.question.Question;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PollSaveRequestDto(
    String title,
    LocalDateTime timeLimit,
    List<String> questionTitles
) {
    public Poll toPollEntity() {
        return Poll.builder()
            .title(title())
            .timeLimit(timeLimit())
            .build();
    }

    public List<Question> toQuestionEntityList(Poll poll) {
        return questionTitles.stream()
            .map(title -> new Question(title, poll))
            .toList();
    }
}
