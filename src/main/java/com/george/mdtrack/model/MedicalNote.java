package com.george.mdtrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "medical_notes")
public class MedicalNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private User doctor;


    @ManyToOne
    @JoinColumn(name = "patient_id")
    private User patient;

    private String noteTitle;
    @Column(length = 4000)
    private String noteBody;


    private LocalDateTime timeStamp;

    @PrePersist
    void onCreate() {
        timeStamp = LocalDateTime.now();
    }
}
