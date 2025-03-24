package com.george.mdtrack.repository;

import com.george.mdtrack.model.MedicalDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalDocumentRepo extends JpaRepository<MedicalDocument, Long> {

    List<MedicalDocument> getByUserId(Long userId);
}
