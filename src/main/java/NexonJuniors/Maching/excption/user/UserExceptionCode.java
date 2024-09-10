package NexonJuniors.Maching.excption.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum UserExceptionCode {
    EXIST_USER(HttpStatus.BAD_REQUEST, "이미 가입된 이메일 입니다."),
    IS_NOT_VALID_CODE(HttpStatus.BAD_REQUEST, "잘못된 이메일 인증 코드입니다."),
    NOT_EXIST_USER(HttpStatus.BAD_REQUEST, "회원정보가 일치하지 않습니다."),
    IS_NOT_VALID_PASSWORD(HttpStatus.BAD_REQUEST, "회원정보가 일치하지 않습니다.");

    private final HttpStatus status;
    private final String message;
}
