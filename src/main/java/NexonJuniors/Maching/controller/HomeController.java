package NexonJuniors.Maching.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// 기본 URL(/)로 접근했을 때 home.html을 보여주는 컨트롤러

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "home"; // resources/templates/home.html을 렌더링
    }

    @GetMapping("/info")
    public String info(){ return "character";}

    @GetMapping("/chatroom")
    public String chatroom(){ return "chatting";}

    @GetMapping("/admin")
    public String admin() { return "admin";}

    @GetMapping("/signInPage")
    public String signIn() {return "signIn";}

    @GetMapping("/signUpPage")
    public String signUp() {return "signUp";}
}