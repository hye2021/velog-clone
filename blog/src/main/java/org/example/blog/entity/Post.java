package org.example.blog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts")
@NoArgsConstructor // JPA에서는 기본 생성자가 필수이다.
@AllArgsConstructor // DTO를 위한 생성자
@Getter @Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // 하나의 User는 여러 개의 Post를 가질 수 있다.
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "publish_status", nullable = false)
    private boolean publishStatus;

    @ManyToOne // 하나의 Series는 여러 개의 Post를 가질 수 있다.
    @JoinColumn(name = "series_id")
    private Series series;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "update_time", nullable = false)
    private Timestamp updateTime;

    @Column(name = "thumbnail_path")
    private String thumbnailPath;
    
    @ManyToMany(fetch = FetchType.LAZY) // Post 엔티티를 로드할 때 관련 모든 'Tag' 엔티티로 지연 로드함
    @JoinTable ( // 중간 테이블 정의
            name = "tags_posts",
            joinColumns = @JoinColumn(name = "post_id"), // 중간 테이블에서 post_id라는 posts의 id를 참조한 외래키를 사용함
            inverseJoinColumns = @JoinColumn(name = "tag_id") // 반대 엔티티인 Tag와 중간 테이블 사이의 외래키 관계
    )
    private Set<Tag> tags = new HashSet<>();
}