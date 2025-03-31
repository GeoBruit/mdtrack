package com.george.mdtrack.repository;

import com.george.mdtrack.model.MedicalNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalNoteRepo extends JpaRepository<MedicalNote, Long> {

    List<MedicalNote> findByPatientIdOrderByTimeStampDesc(Long patientId); //Getting all notes a patient received
    List<MedicalNote> findByDoctorIdOrderByTimeStampDesc(Long doctorId);//Getting all notes a doctor wrote
}
