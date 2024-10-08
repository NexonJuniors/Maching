package NexonJuniors.Maching.excption.matching;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum MatchingExceptionCode {
    ALREADY_PARTICIPATING_USER("이미 매칭에 참여 중인 유저입니다.");

    @Getter
    private final String message;
}
