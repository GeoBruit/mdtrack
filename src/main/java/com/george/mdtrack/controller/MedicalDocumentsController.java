package com.george.mdtrack.controller;

import com.george.mdtrack.dto.MedicalFileToBeSavedDTO;
import com.george.mdtrack.model.MedicalDocument;
import com.george.mdtrack.model.User;
import com.george.mdtrack.service.MedicalDocumentService;
import com.george.mdtrack.service.UserService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


/**
 * Controller responsible for managing medical documents.
 * Provides endpoints for uploading, viewing, downloading, and deleting documents.
 */
@Controller
@RequestMapping("/medical-documents/")
public class MedicalDocumentsController {


    MedicalDocumentService medicalDocumentService;
    UserService userService;
    /**
     * Constructor-based dependency injection of services.
     */
    public MedicalDocumentsController(MedicalDocumentService medicalDocumentService, UserService userService) {

        this.medicalDocumentService = medicalDocumentService;
        this.userService = userService;
    }

    /**
     * Handles POST request to upload a medical document.
     * Retrieves the currently logged-in user and saves the uploaded file.
     *
     * @param medicalFileToBeSavedDTO DTO containing file metadata and content
     * @return redirect to the medical documents page
     */
    @PostMapping("/upload")
    String saveMedicalDocument(@ModelAttribute MedicalFileToBeSavedDTO medicalFileToBeSavedDTO){

        //Getting the logged in user
        User loggedinUser = userService.getUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());

        medicalDocumentService.saveMedicalDocument(medicalFileToBeSavedDTO, loggedinUser.getId());

        return "redirect:/medical-documents";
    }
    /**
     * Handles GET request to view a medical document in the browser.
     * Serves the file inline if it exists.
     *
     * @param id ID of the medical document
     * @return the file as a Resource in the HTTP response
     * @throws IOException if file cannot be read or found
     */
    @GetMapping("/view/{id}")
    public ResponseEntity<Resource> viewDocument(@PathVariable Long id) throws IOException {
        MedicalDocument doc = medicalDocumentService.getMedicalDocumentById(id);

        Path filePath = Paths.get(doc.getFilePath());
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        Resource resource = new FileSystemResource(filePath);
        String mimeType = Files.probeContentType(filePath);
        if (mimeType == null) {
            mimeType = "application/octet-stream"; // fallback
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + doc.getFileName() + "\"")
                .body(resource);
    }

    /**
     * Handles GET request to download a medical document.
     * Sends the file as an attachment for download.
     *
     * @param id ID of the medical document
     * @return the file as a downloadable Resource
     * @throws IOException if file cannot be found or accessed
     */

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) throws IOException {
        MedicalDocument doc = medicalDocumentService.getMedicalDocumentById(id);
        Path filePath = Paths.get(doc.getFilePath());

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        Resource resource = new FileSystemResource(filePath);
        String mimeType = Files.probeContentType(filePath);
        if (mimeType == null) mimeType = "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFileName() + "\"")
                .body(resource);
    }

    /**
     * Handles POST request to delete a medical document by ID.
     *
     * @param id ID of the document to be deleted
     * @return redirect to the medical documents list page
     */
    @PostMapping("/delete/{id}")
    public String deleteDocument(@PathVariable Long id){

        medicalDocumentService.deleteMedicalDocument(id);
        return "redirect:/medical-documents";
    }

}
