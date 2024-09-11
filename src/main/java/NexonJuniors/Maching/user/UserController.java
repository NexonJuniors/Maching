package NexonJuniors.Maching.user;

import NexonJuniors.Maching.user.dto.EmailAuthDto;
import NexonJuniors.Maching.user.dto.SignInRequestDto;
import NexonJuniors.Maching.user.dto.SignInResponseDto;
import NexonJuniors.Maching.user.dto.SignUpDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    // 로그인 요천 URL
    @PostMapping("/signIn")
    public SignInResponseDto singIn(@RequestBody SignInRequestDto dto){
        return userService.signIn(dto);
    }
}
