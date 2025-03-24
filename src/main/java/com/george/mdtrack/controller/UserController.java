package com.george.mdtrack.controller;


import com.george.mdtrack.dto.UserRegisterDTO;
import com.george.mdtrack.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/")
    String landingPage() {

        return "landing-page";
    }

    @GetMapping("/home")
    String index() {

        return "index";
    }

    @GetMapping("/login-form")
    String loginForm() {
        return "form/login-form";
    }

    @GetMapping("/register-form")
    String registerForm(Model model) {

        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();

        model.addAttribute("user", userRegisterDTO);
        return "form/register-form";
    }

    @PostMapping("/register")
    String register(@ModelAttribute UserRegisterDTO userRegisterDTO) {

        userService.saveUser(userRegisterDTO);
        return "redirect:/login-form";
    }
}
