package online.poll.web.exception;

public class PollNotExistException extends RuntimeException{
    public PollNotExistException(){
        super("Poll is not exist");
    }
}
