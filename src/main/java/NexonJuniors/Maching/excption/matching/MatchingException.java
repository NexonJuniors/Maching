package NexonJuniors.Maching.excption.matching;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MatchingException extends RuntimeException {

    @Getter
    private final MatchingExceptionCode code;
}
