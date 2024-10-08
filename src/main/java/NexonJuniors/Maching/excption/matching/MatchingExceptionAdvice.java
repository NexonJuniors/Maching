package NexonJuniors.Maching.excption.matching;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;


@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class MatchingExceptionAdvice {
    private final SimpMessagingTemplate simpMessagingTemplate;
    @MessageExceptionHandler(MatchingException.class)
    public void matchingException(MatchingException exception){
        String roomId = exception.getRoomId();
        String characterName = exception.getCharacterName();
        String message = exception.getCode().getMessage();

        HashMap<String, String> errorMessage = new HashMap<>();
        errorMessage.put("errorMessage", message);

        log.warn(String.format("%s | %s 님 | %s", exception.getCode().name(), characterName, message));
        simpMessagingTemplate.convertAndSend(
                String.format("/room/%s", roomId),
                errorMessage
        );
    }
}
