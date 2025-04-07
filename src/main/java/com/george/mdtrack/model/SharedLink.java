package com.george.mdtrack.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.usertype.internal.OffsetDateTimeCompositeUserType;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SharedLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime timeStamp;

    private String link;

    @ManyToOne(cascade = CascadeType.ALL)
    private User user;


}
