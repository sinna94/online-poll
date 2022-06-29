package online.poll.websocket.message;

import lombok.Builder;

@Builder
public record RequestMessage(
    Long pollId,
    PollType pollType,
    Long questionId
) {}
