package online.poll.web;

import org.springframework.messaging.simp.stomp.*;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;

class MessageFrameHandler<T> extends StompSessionHandlerAdapter {
    private final Class<T> tClass;
    private final BlockingQueue<T> queue;

    public MessageFrameHandler(final Class<T> tClass, final BlockingQueue<T> queue) {
        this.tClass = tClass;
        this.queue = queue;
    }

    @Override
    public Type getPayloadType(final StompHeaders headers) {
        return tClass;
    }

    @Override
    public void handleFrame(final StompHeaders headers, final Object payload) {
        queue.offer((T)payload);
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        exception.printStackTrace();
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        exception.printStackTrace();
    }
}
