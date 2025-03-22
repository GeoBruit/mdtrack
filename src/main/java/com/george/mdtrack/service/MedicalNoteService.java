package com.george.mdtrack.service;

import com.george.mdtrack.dto.MedicalNoteDTO;
import com.george.mdtrack.model.MedicalNote;
import com.george.mdtrack.model.User;
import com.george.mdtrack.repository.MedicalNoteRepo;
import com.george.mdtrack.repository.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class MedicalNoteService {

    //DATA BASE access
    private final UserRepo userRepo;
    private final MedicalNoteRepo medicalNoteRepo;


    public MedicalNoteService(UserRepo userRepo, MedicalNoteRepo medicalNoteRepo) {
        this.userRepo = userRepo;
        this.medicalNoteRepo = medicalNoteRepo;
    }




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

    private MedicalNote parseMedicalNoteDTOToMedicalNoteEntity(MedicalNoteDTO medicalNoteDTO){
        //create medical to be saved
        MedicalNote medicalNote = new MedicalNote();
        //set the title and body
        medicalNote.setNoteTitle(medicalNoteDTO.noteTitle());
        medicalNote.setNoteBody(medicalNoteDTO.noteBody());

        return medicalNote;

    }


}
