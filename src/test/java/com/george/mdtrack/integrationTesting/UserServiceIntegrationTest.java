package com.george.mdtrack.integrationTesting;


import com.george.mdtrack.dto.MedicalNoteDTO;
import com.george.mdtrack.dto.UserRegisterDTO;
import com.george.mdtrack.model.MedicalNote;
import com.george.mdtrack.model.User;
import com.george.mdtrack.repository.MedicalNoteRepo;
import com.george.mdtrack.repository.UserRepo;
import com.george.mdtrack.service.MedicalNoteService;
import com.george.mdtrack.service.UserService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Objects;

@SpringBootTest
public class UserServiceIntegrationTest {


    @Autowired
    private MedicalNoteService medicalNoteService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private MedicalNoteRepo medicalNoteRepo;


    @Test
    public void testSaveUser() {

        //Creating user dto to save to the database
        UserRegisterDTO newUserDTO = new UserRegisterDTO();
        newUserDTO.setUsername("Patient");
        newUserDTO.setEmail("patient@example.com");
        newUserDTO.setPassword("password");

        User savedUser = userService.saveUser(newUserDTO);

        assert savedUser != null;
        assert Objects.equals(savedUser.getUsername(), newUserDTO.getUsername());
        assert Objects.equals(savedUser.getEmail(), newUserDTO.getEmail());

    }

    @Test
    public void testSaveUserWithExistingEmail() {

        //Creating user dto to save to the database (This user already exists)
        UserRegisterDTO newUserDTO = new UserRegisterDTO();
        newUserDTO.setUsername("Username");
        newUserDTO.setEmail("testuser@example.com");
        newUserDTO.setPassword("password");


        //Should Throw exception if email already exists
        assertThrows(RuntimeException.class, () -> userService.saveUser(newUserDTO));

    }


    @Test
    public void testSaveUserWithNullValues() {
        UserRegisterDTO newUserDTO = new UserRegisterDTO();
        newUserDTO.setUsername(null);
        newUserDTO.setEmail(null);
        newUserDTO.setPassword(null);

        assertThrows(IllegalArgumentException.class, () ->
                userService.saveUser(newUserDTO));
    }

    @Test
    public void testSaveUserWithEmptyValues() {
        UserRegisterDTO newUserDTO = new UserRegisterDTO();
        newUserDTO.setUsername("");
        newUserDTO.setEmail("");
        newUserDTO.setPassword("");

        assertThrows(IllegalArgumentException.class, () ->
                userService.saveUser(newUserDTO));
    }


    @Test
    public void testGetUserByIdThrowsError(){

        Long id = 1000L; //setting an id that does not exists

        assertThrows(IllegalArgumentException.class, () ->
                userService.getUserByUserId(id));
    }


    @Test
    public void testGetUserByUserId(){

        Long id = 1L; //this id exists in the database

        User user = userService.getUserByUserId(id);

        assertNotNull(user);
        assertEquals(id, user.getId());
    }

    @Test
    public void testAddMedicalNote(){

        //Real ids from testing database
        Long patientId = 1L;
        Long doctorId = 4L;

        //Create medical note DTO
        MedicalNoteDTO medicalNoteDTO =  new MedicalNoteDTO("Test note title 4", "Test note body 4");

        medicalNoteService.saveMedicalNote(doctorId, patientId, medicalNoteDTO);

        MedicalNote savedMedicalMote = medicalNoteRepo.getReferenceById(2L); //2L is the Id that will be assigned for this test

        assertNotNull(savedMedicalMote);
    }



    @Test
    public void testGetMedicalNotesByPatientId(){
        Long userId = 1L; //Id for patient in testing database

        List<MedicalNote> medicalNotes = userService.getMedicalNotesByUserId(userId);

        assertNotNull(medicalNotes);

    }
}
