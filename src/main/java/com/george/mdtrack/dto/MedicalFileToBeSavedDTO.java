package com.george.mdtrack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalFileToBeSavedDTO{

    String fileName;
    String fileType;
    MultipartFile multipartFile;
}
