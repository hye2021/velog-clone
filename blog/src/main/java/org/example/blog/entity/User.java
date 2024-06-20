package org.example.blog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Setter @Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String name;
    private String email;
    private LocalDate registration_date;
    private String image_path;

    public User(String username, String password, String name, String email, LocalDate registration_date) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.registration_date = registration_date;
    }

}
