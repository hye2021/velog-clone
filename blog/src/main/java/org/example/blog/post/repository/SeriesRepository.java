package org.example.blog.post.repository;

import org.example.blog.post.entity.Series;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeriesRepository extends JpaRepository<Series, Long> {
    List<Series> findByUserId(Long userId);
}
