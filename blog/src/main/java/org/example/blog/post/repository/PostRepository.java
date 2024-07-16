package org.example.blog.post.repository;

import org.example.blog.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByUserUsername(String username, Pageable pageable);

    // JPQL + Spring Data JPA
    @Query(value = "SELECT p.* FROM posts p " +
            "LEFT JOIN likes l ON p.id = l.post_id " +
            "WHERE p.update_time >= :startTime AND p.publish_status = true " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(l.user_id) DESC",
            nativeQuery = true)
    Page<Post> findTrendingPosts(@Param("startTime") Timestamp startTime, Pageable pageable);

    List<Post> findByUserUsernameAndPublishStatus(String username, boolean publishStatus);
}
