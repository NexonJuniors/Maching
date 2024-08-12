package NexonJuniors.Maching.excption.matching;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@RestControllerAdvice
public class MatchingExceptionAdvice {
    @ExceptionHandler(MatchingException.class)
    public ResponseEntity apiException(MatchingException exception){
        HttpStatus status = exception.getCode().getStatus();
        String message = exception.getCode().getMessage();

        HashMap<String, String> response = new HashMap<>();
        response.put("message", message);

        return ResponseEntity.status(status).body(response);
    }
}
