package online.poll.domain.question;

import lombok.Getter;
import online.poll.domain.BaseTimeEntity;
import online.poll.domain.poll.Poll;

import javax.persistence.*;

@Getter
@Entity
public class Question extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "QUESTION_ID")
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(nullable = false)
    private Long voteCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POLL_ID")
    private Poll poll;

    public Question(String title, Poll poll) {
        this.title = title;
        this.voteCount = 0L;
        this.poll = poll;
    }

    public Question() {
    }
}
