package NexonJuniors.Maching.user;

import NexonJuniors.Maching.user.dto.EmailAuthDto;
import NexonJuniors.Maching.user.dto.SignInRequestDto;
import NexonJuniors.Maching.user.dto.SignInResponseDto;
import NexonJuniors.Maching.user.dto.SignUpDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 회원가입 URL
    @PostMapping("/user")
    public void registerUser(@RequestBody SignUpDto dto){
        userService.registerUser(dto);
    }

    // 이메일 인증 요청 URL
    @PostMapping("/emailAuth")
    public void IssueEmailAuthCode(@RequestBody EmailAuthDto dto){
        userService.IssueEmailAuthCode(dto);
    }

    // 로그인 요청 URL
    @PostMapping("/signIn")
    public ResponseEntity<?> singIn(@RequestBody SignInRequestDto dto){
        SignInResponseDto signInResponseDto = userService.signIn(dto);

        ResponseCookie refreshToken = ResponseCookie
                .from("refreshToken", signInResponseDto.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .build();

        ResponseCookie accessToken = ResponseCookie
                .from("accessToken", signInResponseDto.getAccessToken())
                .build();

        HashMap<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "로그인 되었습니다.");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshToken.toString())
                .header(HttpHeaders.SET_COOKIE, accessToken.toString())
                .body(responseBody);
    }

    // 로그아웃 요청 URL
    @PostMapping("/signOut")
    public ResponseEntity<?> signOut(@RequestHeader("Authorization") String token){
        userService.signOut(token);

        ResponseCookie refreshToken = ResponseCookie
                .from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .maxAge(0)
                .build();

        ResponseCookie accessToken = ResponseCookie
                .from("accessToken", "")
                .maxAge(0)
                .build();

        HashMap<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "로그아웃 되었습니다.");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshToken.toString())
                .header(HttpHeaders.SET_COOKIE, accessToken.toString())
                .body(responseBody);
    }
}
