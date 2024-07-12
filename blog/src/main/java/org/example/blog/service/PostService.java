package org.example.blog.service;

import lombok.extern.slf4j.Slf4j;
import org.example.blog.entity.Post;
import org.example.blog.entity.Series;
import org.example.blog.entity.Tag;
import org.example.blog.entity.User;
import org.example.blog.repository.PostRepository;
import org.example.blog.repository.SeriesRepository;
import org.example.blog.repository.TagRepository;
import org.example.blog.statics.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@Service
@Slf4j
public class PostService {
    @Autowired private PostRepository postRepository;
    @Autowired private TagRepository tagRepository;
    @Autowired private SeriesRepository seriesRepository;

    @Transactional(readOnly = true)
    public List<Post> getRecentPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updateTime").descending()
                        .and(Sort.by("id").descending()));
        Page<Post> postPage =  postRepository.findAll(pageable);
        return postPage.getContent();
    }

    @Transactional
    public Post savePost(Post post) {
        post.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        return postRepository.save(post);
    }

    @Transactional
    public String saveThumbnailImg(MultipartFile thumbnail, String username) {
        String thumbnailPath = "/thumbnails/" + username;
        return SaveImageAs(thumbnail, thumbnailPath);
    }

    public String saveImage(MultipartFile image, String username) {
        String imagePath = "/images/" + username;
        return SaveImageAs(image, imagePath);
    }

    public String SaveImageAs(MultipartFile image, String path) {
        File pathDir = new File(Constants.IMAGE_PAHT + path);

        // 디렉토리가 존재하지 않으면 생성
        if (!pathDir.exists()) {
            boolean dirsCreated = pathDir.mkdirs();
            if (!dirsCreated) {
                log.error("      can not create directories: " + pathDir);
            }
        }

        // 파일 저장
        File uploadFile = new File(pathDir, image.getOriginalFilename());
        log.info("      uploadFile path: " + uploadFile.getAbsolutePath());
        try {
            image.transferTo(uploadFile);
            return path + "/" + image.getOriginalFilename();
        } catch (Exception e) {
            log.error("      can not upload the thumbnail image: " + e);
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
