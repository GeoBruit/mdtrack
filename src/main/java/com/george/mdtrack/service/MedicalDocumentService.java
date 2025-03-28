package com.george.mdtrack.service;

import com.george.mdtrack.dto.MedicalFileToBeSavedDTO;
import com.george.mdtrack.model.MedicalDocument;
import com.george.mdtrack.model.User;
import com.george.mdtrack.repository.MedicalDocumentRepo;
import com.george.mdtrack.repository.MedicalNoteRepo;
import com.george.mdtrack.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@Service
public class MedicalDocumentService {


    private final UserRepo userRepo;
    private final MedicalDocumentRepo medicalDocumentRepo;

    @Value("${file.upload-dir}") //Root directory for the files to be store (declared in application.properties)
    private String medicalDocumentFolder;

    public MedicalDocumentService(UserRepo userRepo, MedicalDocumentRepo medicalDocumentRepo) {

        this.userRepo = userRepo;
        this.medicalDocumentRepo = medicalDocumentRepo;
    }


    @Transactional
    public void deleteMedicalDocument(Long id){
        medicalDocumentRepo.deleteById(id);
    }

    /**
     * Retrieves all medical documents associated with a given user ID.
     *
     * @param userId the ID of the user whose medical documents are to be retrieved
     * @return a list of medical documents associated with the specified user ID
     * @throws IllegalArgumentException if no medical documents are found for the given user ID
     */
    public List<MedicalDocument> getAllMedicalDocumentsByUserId(Long userId){

        List<MedicalDocument> medicalDocuments = medicalDocumentRepo.getByUserId(userId);
        if(medicalDocuments.isEmpty()){
            //returning an empty list if the user has no medical documents
            return Collections.emptyList();
        }
        return medicalDocuments;

    }

    /**
     * Retrieves a medical document by its unique identifier.
     *
     * @param id the unique identifier of the medical document to retrieve
     * @return the medical document associated with the specified id
     * @throws IllegalArgumentException if no medical document with the specified id is found
     */
    public MedicalDocument getMedicalDocumentById(Long id){
        return medicalDocumentRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Medical Document with id " + id + " not found"));
    }

    /**
     * Saves a medical document associated with a specific user. This method takes in the details
     * of the medical file, associates it with a user, and saves the document in the database, as well
     * as the local filesystem.
     *
     * @param medicalFileToBeSavedDTO an object containing details about the medical file to be saved, including
     *                                the file itself, its name, and file type
     * @param userId the unique identifier of the user who owns the medical document
     * @throws IllegalArgumentException if the file is null or if the user with the given ID is not found
     * @throws RuntimeException if an error occurs during the saving process
     */
    public void saveMedicalDocument(MedicalFileToBeSavedDTO medicalFileToBeSavedDTO, Long userId){

        try{
            //checking if the file is null
            if(medicalFileToBeSavedDTO.getMultipartFile() == null){
                throw new IllegalArgumentException("File is null");
            }

            //Setting the file Path for the document
            Path filePathToBeSavedInDataBase = createFilePath(medicalFileToBeSavedDTO.getMultipartFile(), userId);
            //The user owning the file
            User user = userRepo.findById(userId).orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));

            MedicalDocument documentToBeSaved = new MedicalDocument();
            documentToBeSaved.setFileName(medicalFileToBeSavedDTO.getFileName());
            documentToBeSaved.setFilePath(filePathToBeSavedInDataBase.toString());
            documentToBeSaved.setFileType(medicalFileToBeSavedDTO.getFileType());
            documentToBeSaved.setUser(user);

            //Saving the file to the DataBase
            medicalDocumentRepo.save(documentToBeSaved);
            //Saving the file to local storage
            saveMedicalDocumentToLocalStorage(medicalFileToBeSavedDTO.getMultipartFile(), filePathToBeSavedInDataBase);


        }catch (Exception e){
            throw new RuntimeException("Error saving medical document to database");
        }

    }

    /**
     * Saves a provided medical document file to a specified directory on the local storage.
     * If the file already exists at the specified path, it will be replaced.
     *
     * @param file the medical document file to be saved as a MultipartFile
     * @param filePath the file path where the document should be saved on the local storage
     */
    private void saveMedicalDocumentToLocalStorage(MultipartFile file, Path filePath){
        try{
            //saving the file to the upload directory
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        }catch (IOException e){

            Logger.getLogger(MedicalDocumentService.class.getName()).warning("Error saving file to local storage");
        }

    }


    /**
     * Creates a file path where a given file should be stored based on the user ID.
     * Ensures that a folder for the user exists and returns the full path where the file can be saved.
     *
     * @param file the file to be stored as a MultipartFile
     * @param userId the ID of the user to whom the file belongs
     * @return the file path as a Path object where the file is to be stored
     * @throws RuntimeException if an error occurs while creating the file path
     */
    public Path createFilePath(MultipartFile file, Long userId){

        try{
            //Creating the file path to store the file at and store to the db
            Path userFolder = Paths.get(medicalDocumentFolder,"user_"+ String.valueOf(userId)).toAbsolutePath().normalize();
            //Clean the file name to prevent security risks
            String fileName = StringUtils.cleanPath(file.getOriginalFilename()); //TODO make sure the file is not null

            //Checking if the user foler already exists
            //if it doesnt we create it
            if(!userFolder.toFile().exists()){
                userFolder.toFile().mkdirs();
            }


            //File Path to be saved in the db
            return userFolder.resolve(fileName);
        } catch (Exception e){
            throw new RuntimeException("Error creating file path");
        }
    }

}
