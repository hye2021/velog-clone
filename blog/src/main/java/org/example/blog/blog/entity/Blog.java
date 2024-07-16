package org.example.blog.blog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.blog.user.entity.User;

@Entity
@Table(name = "blogs")
@NoArgsConstructor // JPA에서는 기본 생성자가 필수이다.
@Getter @Setter
public class Blog {
    @Id
    @Column(name = "user_id") // 외래키 이자 기본키
    private Long userId;

    @OneToOne // 하나의 User는 하나의 Blog를 가질 수 있다.
    @MapsId // user_id를 기본키로 사용
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    // title의 기본 값은 user의 username
    @PrePersist // 엔티티가 영속화되기 전에 실행
    public void prePersist() {
        if (this.title == null && this.user != null) {
            this.title = this.user.getUsername();
        }
    }

}
