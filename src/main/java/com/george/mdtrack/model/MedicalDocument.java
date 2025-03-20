package com.george.mdtrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "medical_documents")
public class MedicalDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Used to generate the use id for us
    private Long id;

    private String fileName;
    private String filePath;
    private String fileType;
    private LocalDateTime timeStamp;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}

