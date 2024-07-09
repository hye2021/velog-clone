package org.example.blog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tags")
@Getter @Setter
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne // 하나의 User는 여러 개의 Tag를 가질 수 있다.
    @JoinColumn(name = "user_id", nullable = false) // 외래키 정의
    private User user;
}
