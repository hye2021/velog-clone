package org.example.blog.post.entity;

import jakarta.persistence.*;
import org.example.blog.user.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne // 여러 개의 자식 댓글이 하나의 부모 댓글에 속한다.
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "content", nullable = false)
    private String content;

    @OneToMany(mappedBy = "parent")
    private List<Comment> replies = new ArrayList<>();

    // upload time?
}
