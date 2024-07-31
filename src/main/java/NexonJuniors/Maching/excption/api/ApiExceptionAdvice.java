package NexonJuniors.Maching.excption.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

// Api 관련 Exception Handler 모아 놓은 클래스

@RestControllerAdvice
public class ApiExceptionAdvice {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity apiException(ApiException exception){
        HttpStatus status = exception.getCode().getStatus();
        String message = exception.getCode().getMessage();

        HashMap<String, String> response = new HashMap<>();
        response.put("message", message);

        return ResponseEntity.status(status).body(response);
    }
}
