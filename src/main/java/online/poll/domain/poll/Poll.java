package online.poll.domain.poll;

import lombok.Builder;
import lombok.Getter;
import online.poll.domain.BaseTimeEntity;
import online.poll.domain.question.Question;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
public class Poll extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POLL_ID")
    private Long id;

    @Column(length = 200, nullable = false)
    private String title;

    @Column
    private LocalDateTime timeLimit;

    @Column(nullable = false)
    private boolean completed;

    @OneToMany(mappedBy = "poll")
    private final List<Question> questionList = new ArrayList<>();

    @Builder
    public Poll(String title, LocalDateTime timeLimit) {
        this.title = title;
        this.timeLimit = timeLimit;
        completed = false;
    }

    public Poll() {
    }

    public void addQuestions(List<Question> questions) {
        questionList.addAll(questions);
    }
}
