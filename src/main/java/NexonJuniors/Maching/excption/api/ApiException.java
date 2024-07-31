package NexonJuniors.Maching.excption.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// Api 관련 예외 클래스
@RequiredArgsConstructor
public class ApiException extends RuntimeException {

    @Getter
    private final ApiExceptionCode code;
}
