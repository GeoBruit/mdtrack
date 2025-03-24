package com.george.mdtrack.repository;

import com.george.mdtrack.model.MedicalDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalDocumentRepo extends JpaRepository<MedicalDocument, Long> {
}
