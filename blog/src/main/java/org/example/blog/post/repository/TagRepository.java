package org.example.blog.post.repository;

import org.example.blog.post.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByNameAndUserId(String name, Long userId);

    List<Tag> findAllByUserId(Long userId);
}
