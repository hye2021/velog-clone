package org.example.blog.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.blog.user.entity.User;
import java.util.Set;
import java.util.HashSet;

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

    @ManyToMany(mappedBy = "tags") // Post 엔티티의 tags 필드에 의해 매핑됨
    private Set<Post> posts = new HashSet<>();
}
