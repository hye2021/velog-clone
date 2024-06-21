package org.example.blog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URL;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

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
    @Column(name = "registration_date")
    private Timestamp registrationDate;
    @Column(name = "image_path")
    private URL imagePath;

    @ManyToMany(fetch = FetchType.EAGER) // User 엔티티를 로드할 때 관련 모든 'Role' 엔티티로 즉시 로드함
    @JoinTable( // 중간 테이블 정의
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"), // 중간 테이블에서 user_id라는 users의 id를 참조한 외래키를 사용함
        inverseJoinColumns = @JoinColumn(name = "role_id") // 반대 엔티티인 Role과 중간 테이블 사이의 외래키 관계
    )
    private Set<Role> roles = new HashSet<>();

    public User(String username, String password, String name, String email, Timestamp registrationDate) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.registrationDate = registrationDate;
    }
}
