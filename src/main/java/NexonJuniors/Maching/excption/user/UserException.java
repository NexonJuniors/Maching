package NexonJuniors.Maching.excption.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UserException extends RuntimeException{
    private final UserExceptionCode code;
}
