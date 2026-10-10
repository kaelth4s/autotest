package ru.kaelth4s.db.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Table(name = "session")
@Getter
@Setter
@NoArgsConstructor
public class SessionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "session_key", length = 1000)
    private String sessionKey;

    @Column(name = "created_time")
    private Timestamp createdTime;

    @Column(name = "build_number")
    private Long buildNumber;

    public SessionModel(String sessionKey, Timestamp createdTime, Long buildNumber) {
        this.sessionKey = sessionKey;
        this.createdTime = createdTime;
        this.buildNumber = buildNumber;
    }
}