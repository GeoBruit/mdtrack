package com.george.mdtrack.service;

import com.george.mdtrack.dto.MedicalNoteDTO;
import com.george.mdtrack.model.MedicalNote;
import com.george.mdtrack.model.User;
import com.george.mdtrack.repository.MedicalNoteRepo;
import com.george.mdtrack.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MedicalNoteServiceTest {

    /*
     * Mocks for dependencies used by MedicalNoteService
     */
    @Mock
    private UserRepo userRepo;

    @Mock
    private MedicalNoteRepo medicalNoteRepo;

    /*
     * The service we're testing, with the above mocks injected
     */
    @InjectMocks
    private MedicalNoteService medicalNoteService;

    /*
     * Initializes the mocks before each test case
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /*
     * Test that a medical note is successfully saved when valid doctor and patient IDs are provided
     */
    @Test
    void testSaveMedicalNote_Success() {
        Long doctorId = 1L;
        Long patientId = 2L;

        // Prepare mock users
        User doctor = new User();
        doctor.setId(doctorId);
        User patient = new User();
        patient.setId(patientId);

        // Prepare DTO
        MedicalNoteDTO dto = new MedicalNoteDTO();
        dto.setNoteTitle("Diagnosis");
        dto.setNoteBody("Patient shows symptoms of...");

        // Mock userRepo responses
        when(userRepo.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(userRepo.findById(patientId)).thenReturn(Optional.of(patient));

        // Act
        medicalNoteService.saveMedicalNote(doctorId, patientId, dto);

        // Verify save was called with a MedicalNote that contains the correct data
        verify(medicalNoteRepo, times(1)).save(argThat(note ->
                note.getNoteTitle().equals("Diagnosis") &&
                        note.getNoteBody().equals("Patient shows symptoms of...") &&
                        note.getDoctor().equals(doctor) &&
                        note.getPatient().equals(patient)
        ));
    }

    /*
     * Test that an exception is thrown when the doctor ID is invalid
     */
    @Test
    void testSaveMedicalNote_DoctorNotFound() {
        Long doctorId = 1L;
        Long patientId = 2L;

        when(userRepo.findById(doctorId)).thenReturn(Optional.empty());

        MedicalNoteDTO dto = new MedicalNoteDTO();
        dto.setNoteTitle("Checkup");
        dto.setNoteBody("All good.");

        // Expecting exception handling, but not exception propagation
        assertDoesNotThrow(() -> medicalNoteService.saveMedicalNote(doctorId, patientId, dto));

        verify(medicalNoteRepo, never()).save(any());
    }

    /*
     * Test that an exception is handled when the patient ID is invalid
     */
    @Test
    void testSaveMedicalNote_PatientNotFound() {
        Long doctorId = 1L;
        Long patientId = 2L;

        User doctor = new User();
        doctor.setId(doctorId);

        when(userRepo.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(userRepo.findById(patientId)).thenReturn(Optional.empty());

        MedicalNoteDTO dto = new MedicalNoteDTO();
        dto.setNoteTitle("Note");
        dto.setNoteBody("Body");

        // Should handle exception internally
        assertDoesNotThrow(() -> medicalNoteService.saveMedicalNote(doctorId, patientId, dto));

        verify(medicalNoteRepo, never()).save(any());
    }
}
