package com.george.mdtrack.dto;

/**
 * A Data Transfer Object (DTO) representing a medical note containing a title and body.
 * This class is immutable and uses the Java Record feature to encapsulate the properties.
 * It serves as a lightweight object to transfer note data between processes or layers
 * within the application.
 *
 * Attributes:
 * - noteTitle: The title of the medical note.
 * - noteBody: The body content of the medical note.
 */
public record MedicalNoteDTO(String noteTitle,
                             String noteBody) {
}
