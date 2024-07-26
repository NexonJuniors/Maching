package NexonJuniors.Maching.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// 웹 요청을 처리할 컨트롤러

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Hello, Spring MVC!");
        return "home"; // resources/templates/home.html을 렌더링
    }
}