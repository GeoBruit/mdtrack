package com.george.mdtrack.controller;


import com.george.mdtrack.dto.MedicalFileToBeSavedDTO;
import com.george.mdtrack.dto.MedicalNoteDTO;
import com.george.mdtrack.dto.UserRegisterDTO;
import com.george.mdtrack.model.*;
import com.george.mdtrack.service.MedicalDocumentService;
import com.george.mdtrack.service.MedicalNoteService;
import com.george.mdtrack.service.SharedLinkService;
import com.george.mdtrack.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {

    private final MedicalNoteService medicalNoteService;
    UserService userService;
    MedicalDocumentService medicalDocumentService;
    SharedLinkService sharedLinkService;
    public UserController(UserService userService, MedicalDocumentService medicalDocumentService,
                          MedicalNoteService medicalNoteService, SharedLinkService sharedLinkService) {
        this.userService = userService;
        this.medicalDocumentService = medicalDocumentService;
        this.medicalNoteService = medicalNoteService;
        this.sharedLinkService = sharedLinkService;
    }


    @GetMapping("/user/appointments")
    public String appointments() {

        return "appointments";
    }


    @GetMapping("/user/shared/{id}/{timeStamp}")
    public String accessSharedLink(@PathVariable Long id, @PathVariable LocalDateTime timeStamp, Model model) {

        LocalDateTime now = LocalDateTime.now();

        //Checking if the link has expired (was created more than 24 hrs ago)
        if (now.isAfter(timeStamp.plusHours(24))) {
            return "access-denied";
        }

        return "redirect:/profile/view/" + id;

    }

    @GetMapping("/shared/link/create")
    public String createLink() {

        sharedLinkService.createSharedLink(getLoggedInUserId());



        return "redirect:/user/shared/links";

    }

    @GetMapping("/user/shared/links")
    String getSharedLinks(Model model) {

        Long loggedInUserId = getLoggedInUserId();

        List<SharedLink> userSharedLinks = sharedLinkService.getSharedLinks(loggedInUserId);
        model.addAttribute("userSharedLinks", userSharedLinks);

        return "shared-links";

    }

    @PostMapping("/notes/add/{id}")
    public String addNote(@PathVariable String id, @ModelAttribute MedicalNoteDTO medicalNoteDTO) {

        Long patientId = Long.parseLong(id);
        Long loggedInUserId = getLoggedInUserId();
        medicalNoteService.saveMedicalNote(loggedInUserId, patientId, medicalNoteDTO);

        return "redirect:/profile/view/" + patientId;
    }


    @GetMapping("/profile/view/{id}")
    public String profileView(Model model, @PathVariable Long id) {

        User patient = userService.getUserByUserId(id);
        List<MedicalDocument> patientDocuments = medicalDocumentService.getAllMedicalDocumentsByUserId(id);
        List<MedicalNote> patientNotes = userService.getMedicalNotesByUserId(id);
        MedicalNoteDTO medicalNoteDTO = new MedicalNoteDTO();

        model.addAttribute("medicalNoteDTO", medicalNoteDTO);
        model.addAttribute("patient", patient);
        model.addAttribute("patientDocuments", patientDocuments);
        model.addAttribute("patientNotes", patientNotes);
        model.addAttribute("patientId", patient.getId());

        return "profile-view";
    }


    @GetMapping("/doctor/search")
    String doctorSearch(@RequestParam("query") String query, Model model) {


        List<User> results = userService.searchPatients(query);
        model.addAttribute("searchResults", results);
        model.addAttribute("query", query);

        // So we can re-render the sidebar properly
        boolean isDoctor = checkIfUserIsDoctor();
        model.addAttribute("isDoctor", isDoctor);



        return "doctor-search-results";
    }

    @GetMapping("/profile")
    String showUserProfile(Model model, Principal principal) {

        User loggedInUser = userService.getUserByUsername(principal.getName());
        UserProfile userProfile = userService.getUserProfileByUserId(loggedInUser.getId());
        model.addAttribute("userProfile", userProfile);
        return "user-profile";
    }

    @GetMapping("/profile-form")
    String profile(Model model) {

        User loggedInUser = userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        UserProfile userProfile = userService.getUserProfileByUserId(loggedInUser.getId());

        model.addAttribute("userProfile", userProfile);
        return "form/user-profile-form";
    }

    @PostMapping("/profile/save")
    String saveProfile(@ModelAttribute UserProfile userProfile) {

        User loggedInUser = userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        userService.saveUserProfile(userProfile, loggedInUser.getId());

        return "redirect:/home";
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

       //Check if the user is a doctor
        boolean isDoctor = checkIfUserIsDoctor();

        model.addAttribute("isDoctor", isDoctor);

       model.addAttribute("hasProfile", logedInUser.getUserProfile() != null);
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


    private Boolean checkIfUserIsDoctor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication
                .getAuthorities()
                .stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("DOCTOR"));

    }

    private Long getLoggedInUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = userService.getUserByUsername(authentication.getName());
        return loggedInUser.getId();
    }
}
