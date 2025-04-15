package com.george.mdtrack.service;

import com.george.mdtrack.dto.MedicalNoteDTO;
import com.george.mdtrack.model.MedicalNote;
import com.george.mdtrack.model.User;
import com.george.mdtrack.repository.MedicalNoteRepo;
import com.george.mdtrack.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
/*
 * Service class for handling medical notes in the system.
 * It interacts with the UserRepo and MedicalNoteRepo to perform database operations.
 */
@Service
public class MedicalNoteService {

    //DATA BASE access
    private final UserRepo userRepo;
    private final MedicalNoteRepo medicalNoteRepo;

    /*
     * Constructor for dependency injection of repositories.
     * These repositories are used to access User and MedicalNote data.
     */
    public MedicalNoteService(UserRepo userRepo, MedicalNoteRepo medicalNoteRepo) {
        this.userRepo = userRepo;
        this.medicalNoteRepo = medicalNoteRepo;
    }




    /*
     * Saves a medical note to the database.
     * This method is transactional, meaning all operations inside it will be executed atomically.
     *
     * @param doctorId       ID of the doctor writing the note
     * @param patientId      ID of the patient receiving the note
     * @param medicalNoteDTO Data transfer object containing note details
     */
    @Transactional
    public void saveMedicalNote(Long doctorId, Long patientId, MedicalNoteDTO medicalNoteDTO){
        try{
            //Retrieve the users from the database
            User doctor = userRepo.findById(doctorId).orElseThrow(() -> new IllegalArgumentException("Doctor with id " + doctorId + " not found"));
            User patient = userRepo.findById(patientId).orElseThrow(() -> new IllegalArgumentException("Doctor with id " + doctorId + " not found"));

            MedicalNote medicalNoteToBeSaved = parseMedicalNoteDTOToMedicalNoteEntity(medicalNoteDTO);

            //Set the doctor who wrote the note
            medicalNoteToBeSaved.setDoctor(doctor);
            //Set the patient who will receive the note
            medicalNoteToBeSaved.setPatient(patient);
            //Saving medical note
            medicalNoteRepo.save(medicalNoteToBeSaved);

        }catch (Exception e){

            e.printStackTrace();

        }
    }

    /*
     * Converts a MedicalNoteDTO into a MedicalNote entity.
     *
     * @param medicalNoteDTO DTO containing the title and body of the note
     * @return A MedicalNote entity populated with the given data
     */
    private MedicalNote parseMedicalNoteDTOToMedicalNoteEntity(MedicalNoteDTO medicalNoteDTO){
        //create medical to be saved
        MedicalNote medicalNote = new MedicalNote();
        //set the title and body
        medicalNote.setNoteTitle(medicalNoteDTO.getNoteTitle());
        medicalNote.setNoteBody(medicalNoteDTO.getNoteBody());

        return medicalNote;

    }


}
