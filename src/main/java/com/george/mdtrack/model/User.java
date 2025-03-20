package com.george.mdtrack.model;


import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data //Makes the getters and setter on it s own
@Entity
@Table(name = "users")
public class User implements UserDetails {

    //Delimiter used to split authorities spring
    private static final String AUTHORITY_DELIMITER = ",";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Used to generate the use id for us
    private Long id;


    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;


    //We store user roles as a string (if multiple roles we separate with ",")
    @Column(nullable = false)
    private String userRole;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<MedicalDocument> medicalDocuments;


    @OneToMany(mappedBy = "patient")
    private List<MedicalNote> patientNotes;  // All notes where this user is a patient

    @OneToMany(mappedBy = "doctor")
    private List<MedicalNote> doctorNotes;   //All notes where this user is a doctor


    /**
     * Retrieves the collection of authorities granted to the user.
     * User roles are split by the defined delimiter and transformed
     * into a collection of authorities.
     *
     * @return a collection of authorities associated with the user, each represented
     * as an implementation of {@code GrantedAuthority}.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(this.userRole.split(AUTHORITY_DELIMITER)).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return this.password;
    }


    //Creating all the lists before the user is created.
    @PrePersist
    void createLists() {

        this.patientNotes = new java.util.ArrayList<>();
        this.doctorNotes = new java.util.ArrayList<>();
        this.medicalDocuments = new java.util.ArrayList<>();

    }

}
