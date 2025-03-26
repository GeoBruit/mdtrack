package com.george.mdtrack.controller;


import com.george.mdtrack.dto.UserRegisterDTO;
import com.george.mdtrack.model.MedicalNote;
import com.george.mdtrack.model.User;
import com.george.mdtrack.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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
    String index(Model model){

       String userName = SecurityContextHolder.getContext().getAuthentication().getName();

       User logedInUser = userService.getUserByUsername(userName);
       List<MedicalNote> medicalNotes = userService.getMedicalNotesByUserId(logedInUser.getId());
       model.addAttribute("username", logedInUser.getUsername());
       model.addAttribute("medicalNotes", medicalNotes);

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
