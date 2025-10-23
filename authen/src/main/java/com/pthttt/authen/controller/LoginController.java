package com.pthttt.authen.controller;

import com.pthttt.authen.model.User;
import com.pthttt.authen.repository.UserRepository;
import com.pthttt.authen.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@ModelAttribute("user") User user, Model model) {
        model.addAttribute("message", "Đăng ký thành công! Hãy đăng nhập.");
        userService.createUser(user);
        return "redirect:/login";
    }

    @GetMapping("")
    public String homePage() {
        return "home";
    }
}
