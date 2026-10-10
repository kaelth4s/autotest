package ru.kaelth4s.tests.db.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "author")
@Getter
@Setter
@NoArgsConstructor
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 1000)
    private String name;

    @Column(name = "login", nullable = false, length = 1000, unique = true)
    private String login;

    @Column(name = "email", nullable = false, length = 1000)
    private String email;
}