package com.george.mdtrack.service;

import com.george.mdtrack.dto.MedicalFileToBeSavedDTO;
import com.george.mdtrack.model.MedicalDocument;
import com.george.mdtrack.model.User;
import com.george.mdtrack.repository.MedicalDocumentRepo;
import com.george.mdtrack.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MedicalDocumentServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private MedicalDocumentRepo medicalDocumentRepo;

    @InjectMocks
    private MedicalDocumentService medicalDocumentService;

    private final String testUploadDir = "uploads";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Set the upload directory manually (simulate @Value injection)
        medicalDocumentService = new MedicalDocumentService(userRepo, medicalDocumentRepo);
        // Use reflection or a setter if needed to set the field
        try {
            java.lang.reflect.Field field = MedicalDocumentService.class.getDeclaredField("medicalDocumentFolder");
            field.setAccessible(true);
            field.set(medicalDocumentService, testUploadDir);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set upload directory for test");
        }
    }

    /**
     * Test saving a medical document when all inputs are valid
     */
    @Test
    void testSaveMedicalDocument_Success() {
        Long userId = 1L;
        MockMultipartFile file = new MockMultipartFile("file", "report.pdf", "application/pdf", "dummy content".getBytes());

        MedicalFileToBeSavedDTO dto = new MedicalFileToBeSavedDTO();
        dto.setFileName("report.pdf");
        dto.setFileType("application/pdf");
        dto.setMultipartFile(file);

        User user = new User();
        user.setId(userId);

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        // Act
        assertDoesNotThrow(() -> medicalDocumentService.saveMedicalDocument(dto, userId));

        // Verify repo save was called
        verify(medicalDocumentRepo, times(1)).save(any(MedicalDocument.class));
    }

    /**
     * Test saving a medical document when the file is null
     */
    @Test
    void testSaveMedicalDocument_NullFile_Throws() {
        MedicalFileToBeSavedDTO dto = new MedicalFileToBeSavedDTO();
        dto.setMultipartFile(null);

        assertThrows(RuntimeException.class, () ->
                medicalDocumentService.saveMedicalDocument(dto, 1L));
    }

    /**
     * Test saving a medical document when the user doesn't exist
     */
    @Test
    void testSaveMedicalDocument_UserNotFound_Throws() {
        Long userId = 1L;
        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        MockMultipartFile file = new MockMultipartFile("file", "report.pdf", "application/pdf", "dummy content".getBytes());
        MedicalFileToBeSavedDTO dto = new MedicalFileToBeSavedDTO();
        dto.setFileName("report.pdf");
        dto.setFileType("application/pdf");
        dto.setMultipartFile(file);

        assertThrows(RuntimeException.class, () ->
                medicalDocumentService.saveMedicalDocument(dto, userId));

        verify(medicalDocumentRepo, never()).save(any());
    }

    /**
     * Test fetching documents by user ID
     */
    @Test
    void testGetAllMedicalDocumentsByUserId() {
        Long userId = 1L;
        List<MedicalDocument> docs = List.of(new MedicalDocument(), new MedicalDocument());
        when(medicalDocumentRepo.getByUserIdOrderByTimeStampDesc(userId)).thenReturn(docs);

        List<MedicalDocument> result = medicalDocumentService.getAllMedicalDocumentsByUserId(userId);
        assertEquals(2, result.size());
    }

    /**
     * Test fetching a document by ID when it exists
     */
    @Test
    void testGetMedicalDocumentById_Found() {
        MedicalDocument doc = new MedicalDocument();
        when(medicalDocumentRepo.findById(1L)).thenReturn(Optional.of(doc));

        MedicalDocument result = medicalDocumentService.getMedicalDocumentById(1L);
        assertNotNull(result);
    }

    /**
     * Test fetching a document by ID when it doesn't exist
     */
    @Test
    void testGetMedicalDocumentById_NotFound() {
        when(medicalDocumentRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                medicalDocumentService.getMedicalDocumentById(1L));
    }

    /**
     * Test deleting a document
     */
    @Test
    void testDeleteMedicalDocument() {
        assertDoesNotThrow(() -> medicalDocumentService.deleteMedicalDocument(1L));
        verify(medicalDocumentRepo, times(1)).deleteById(1L);
    }

    /**
     * Test file path creation for a user
     */
    @Test
    void testCreateFilePath_Success() {
        Long userId = 42L;
        MockMultipartFile file = new MockMultipartFile("file", "report.pdf", "application/pdf", "dummy content".getBytes());

        Path result = medicalDocumentService.createFilePath(file, userId);

        assertTrue(result.toString().contains("user_42"));
        assertTrue(result.toString().endsWith("report.pdf"));
    }
}
