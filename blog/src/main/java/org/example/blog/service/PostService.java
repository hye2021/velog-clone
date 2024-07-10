package org.example.blog.service;

import org.example.blog.entity.Post;
import org.example.blog.entity.Series;
import org.example.blog.entity.Tag;
import org.example.blog.entity.User;
import org.example.blog.repository.PostRepository;
import org.example.blog.repository.SeriesRepository;
import org.example.blog.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;

@Service
public class PostService {
    @Autowired private PostRepository postRepository;
    @Autowired private TagRepository tagRepository;
    @Autowired private SeriesRepository seriesRepository;

    @Transactional
    public Post savePost(Post post) {
        post.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        return postRepository.save(post);
    }

    @Transactional
    public String saveThumbnailImg(MultipartFile thumbnail) {
        String thumbnailPath = "images/thumbnails";
        String filename = thumbnail.getOriginalFilename();
        File uploadFile = new File(thumbnailPath, filename);

        try {
            thumbnail.transferTo(uploadFile);
            return uploadFile.getAbsolutePath();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save thumbnail image", e);
        }
    }

    @Transactional
    public Tag saveTag(String name, User user) {
        Tag tag = getTagByUserId(name, user.getId());
        if (tag != null)
            return tag;
        tag = new Tag();
        tag.setName(name.substring(1)); // # 제외
        tag.setUser(user);
        return tagRepository.save(tag);
    }

    @Transactional(readOnly = true)
    public Tag getTagByUserId(String tag, Long userId) {
        return tagRepository.findByNameAndUserId(tag, userId).orElse(null);
    }

    @Transactional
    public Series saveSeries(Series series) {
        return seriesRepository.save(series);
    }

    @Transactional(readOnly = true)
    public Series getSeriesById(Long seriesId) {
        return seriesRepository.findById(seriesId).orElse(null);
    }

}
