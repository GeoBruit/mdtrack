package com.george.mdtrack.integrationTesting;

import com.george.mdtrack.dto.MedicalFileToBeSavedDTO;
import com.george.mdtrack.service.MedicalDocumentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test class for verifying the functionality of MedicalDocumentService
 * related to storing medical files (e.g., images, documents) on disk.
 *
 * This class loads the Spring context and tests the actual behavior of the file-saving
 * service using a real file to simulate a real-world upload.
 *
 * It ensures that the service correctly handles multipart file conversion,
 * storage location resolution, and physical file persistence.
 */
@SpringBootTest
public class MedicalFileServiceTest {


    @Autowired
    private MedicalDocumentService medicalDocumentService;

    /**
     * Integration test that verifies the MedicalDocumentService is able to successfully
     * store a real photo file to the expected location on disk.
     *
     * The test:
     * - Loads a real image file from the local file system.
     * - Wraps it in a MockMultipartFile to simulate file upload.
     * - Calls the service method to save the file.
     * - Asserts the file was saved at the correct location.
     *
     * @throws Exception if an I/O error occurs during test execution
     */
    @Test
    public void storeRealPhotoFile() throws Exception {
        // Path to a real photo file on my machine
        File realFile = new File("C:/Users/georg/Pictures/sample.jpg");
        assertTrue(realFile.exists(), "The test file does not exist!");

        // Create a MultipartFile from it
        try (FileInputStream fis = new FileInputStream(realFile)) {
            MultipartFile multipartFile = new MockMultipartFile(
                    "file",
                    realFile.getName(),
                    Files.probeContentType(realFile.toPath()),
                    fis
            );

            MedicalFileToBeSavedDTO documentToBeSaved = new MedicalFileToBeSavedDTO("Blood Tests", "Blod Tests", multipartFile);

            // Call your actual method
            Long userId = 1L;
            medicalDocumentService.saveMedicalDocument(documentToBeSaved, userId);

            // Build expected path to where the file should have been saved
            Path expectedPath = Paths.get("uploads", "user_" + userId, realFile.getName())
                    .toAbsolutePath().normalize();

            // Check that the file was created
            assertTrue(Files.exists(expectedPath), "File was not saved to disk as expected");
        }
    }

}
