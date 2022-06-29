package online.poll.websocket.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import online.poll.domain.question.Question;

import java.util.List;

public class ResponseMessage {

    public static ResponseMessage createResponseMessage(List<Question> questions){
        return new ResponseMessage(questions);
    }
    private final List<QuestionResult> questionResults;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    private ResponseMessage(@JsonProperty("questionResults") List<Question> questionList){
        questionResults = questionList.stream()
            .map(QuestionResult::createQuestionResult)
            .toList();
    }

    public List<QuestionResult> getQuestionResults() {
        return questionResults;
    }
}
