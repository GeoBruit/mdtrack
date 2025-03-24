package com.george.mdtrack.dto;

import org.springframework.web.multipart.MultipartFile;

public record MedicalFileToBeSavedDTO(String fileName,
                                      String fileType,
                                      MultipartFile file) {
}
