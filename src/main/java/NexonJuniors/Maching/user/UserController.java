package NexonJuniors.Maching.user;

import NexonJuniors.Maching.user.dto.EmailAuthDto;
import NexonJuniors.Maching.user.dto.SignUpDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 회원가입 URL
    @PostMapping("/user")
    public void registerUser(@RequestBody SignUpDto signUpDto){
        userService.registerUser(signUpDto);
    }

    @PostMapping("/emailAuth")
    public void IssueEmailAuthCode(@RequestBody EmailAuthDto emailAuthDto){
        userService.IssueEmailAuthCode(emailAuthDto);
    }
}
