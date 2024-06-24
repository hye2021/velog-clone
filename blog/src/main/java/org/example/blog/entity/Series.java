package org.example.blog.entity;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "series")
@NoArgsConstructor // JPA에서는 기본 생성자가 필수이다.
public class Series {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // 하나의 User는 여러 개의 Series를 가질 수 있다.
    @JoinColumn(name = "user_id", nullable = false)
        // 외래 키를 정의할 때 사용
        // User 엔티티의 @Id를 참조
    private User user;

    @Column(name = "title", nullable = false)
    private String title;
}
