package online.poll.websocket.message;

import lombok.Getter;
import online.poll.domain.question.Question;

@Getter
public class QuestionResult {
    private final String title;
    private final Long voteCount;

    private QuestionResult(
        String title,
        Long voteCount
    ) {
        this.title = title;
        this.voteCount = voteCount;
    }

    public static QuestionResult createQuestionResult(Question question) {
        return new QuestionResult(question.getTitle(), question.getVoteCount());
    }
}
