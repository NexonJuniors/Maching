package NexonJuniors.Maching.excption.matching;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MatchingException extends RuntimeException{
    private final String roomId;
    private final String characterName;
    private final MatchingExceptionCode code;
}
