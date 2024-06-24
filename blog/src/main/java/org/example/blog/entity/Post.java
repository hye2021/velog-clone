package org.example.blog.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "posts")
@NoArgsConstructor // JPA에서는 기본 생성자가 필수이다.
@AllArgsConstructor // DTO를 위한 생성자
@Getter
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
}