package NexonJuniors.Maching.excption.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// Api 관련 예외들을 상수 형태로 저장하는 클래스
// 에러 코드 영어 대문자로 작성, (HTTP 상태코드, 에러 메세지) 형태, 단어 사이에 _ 입력
@RequiredArgsConstructor
public enum ApiExceptionCode {
    NOT_EXIST_USER(HttpStatus.BAD_REQUEST, "존재하지 않는 유저입니다."),
    API_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "넥슨 API 서버 에러입니다."),
    DATA_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 에러입니다.");

    @Getter
    private final HttpStatus status;
    @Getter
    private final String message;
}
