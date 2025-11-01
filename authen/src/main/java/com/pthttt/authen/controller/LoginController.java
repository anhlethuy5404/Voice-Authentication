package com.pthttt.authen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.pthttt.authen.model.User;
import com.pthttt.authen.service.UserService;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {

        if (error != null) {
            model.addAttribute("error", "Sai tên đăng nhập hoặc mật khẩu!");
        }

        if (logout != null) {
            model.addAttribute("message", "Đăng xuất thành công!");
        }

        return "login";
    }

    @GetMapping("/home")
    public String home() {
        return "userHome";
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String register(@ModelAttribute("user") User user, Model model) {
        try {
            userService.createUser(user);
            model.addAttribute("successMessage", "Đăng ký thành công! Hãy đăng nhập.");
            return "redirect:/login?success=true";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("user", user); // Giữ lại thông tin người dùng đã nhập
            return "signup";
        } catch (Exception e) {
            model.addAttribute("error", "Đã xảy ra lỗi khi đăng ký. Vui lòng thử lại sau.");
            model.addAttribute("user", user);
            return "signup";
        }
    }

    @GetMapping("")
    public String homePage() {
        return "login";
    }
}
