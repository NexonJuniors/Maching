package NexonJuniors.Maching.excption.matching;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 에러 코드 영어 대문자로 작성, (HTTP 상태코드, 에러 메세지) 형태, 단어 사이에 _ 입력
@RequiredArgsConstructor
public enum MatchingExceptionCode {
    NOT_EXIST_USER(HttpStatus.BAD_REQUEST, "존재하지 않는 유저입니다."),
    ALREADY_EXISTING_USER(HttpStatus.INTERNAL_SERVER_ERROR, "이미 매칭중인 유저입니다."),
    DATA_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 에러입니다.");

    @Getter
    private final HttpStatus status;
    @Getter
    private final String message;
}
