package com.george.mdtrack.controller;


import com.george.mdtrack.dto.MedicalFileToBeSavedDTO;
import com.george.mdtrack.dto.UserRegisterDTO;
import com.george.mdtrack.model.MedicalDocument;
import com.george.mdtrack.model.MedicalNote;
import com.george.mdtrack.model.User;
import com.george.mdtrack.service.MedicalDocumentService;
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
    MedicalDocumentService medicalDocumentService;
    public UserController(UserService userService, MedicalDocumentService medicalDocumentService) {
        this.userService = userService;
        this.medicalDocumentService = medicalDocumentService;
    }



    @GetMapping("/")
    String landingPage() {

        return "landing-page";
    }

    @GetMapping("/medical-documents")
    String medicalDocuments(Model model) {

        //Getting the logged in user username
        String userNameForLoggedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        //retrieve the User Object
        User logedInUser = userService.getUserByUsername(userNameForLoggedUser);

        //Retrieve the Medical documents for the user
        List<MedicalDocument> medicalDocuments = medicalDocumentService.getAllMedicalDocumentsByUserId(logedInUser.getId());
        //Create new MedicalDocumentDTO for the user to load new docs
        MedicalFileToBeSavedDTO medicalFileToBeSavedDTO = new MedicalFileToBeSavedDTO();

        //injection he objects into the html template
        model.addAttribute("medicalFileToBeSaved", medicalFileToBeSavedDTO);
        model.addAttribute("medicalDocuments", medicalDocuments);
        model.addAttribute("hasProfile", logedInUser.getUserProfile() != null);

        return "medical-documents";

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
