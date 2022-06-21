package online.poll.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(PollNotExistException.class)
    public ResponseEntity<String> pollNotExistExceptionHandler(PollNotExistException exception){
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(exception.getMessage());
    }
}
