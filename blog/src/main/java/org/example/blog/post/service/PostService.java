package org.example.blog.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blog.post.entity.Post;
import org.example.blog.post.entity.Series;
import org.example.blog.post.entity.Tag;
import org.example.blog.user.entity.User;
import org.example.blog.post.repository.PostRepository;
import org.example.blog.post.repository.SeriesRepository;
import org.example.blog.post.repository.TagRepository;
import org.example.blog.statics.Constants;
import org.example.blog.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    private final UserService userService;

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final SeriesRepository seriesRepository;

    public User getUsersByUsername(String username) {
        return userService.getUsersByUsername(username);
    }

    @Transactional(readOnly = true)
    public List<Post> getRecentPosts(int page, int size, String username) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updateTime").descending()
                        .and(Sort.by("id").descending()));

        Page<Post> postPage;
        if (username != null && !username.isEmpty()) {
            postPage = postRepository.findByUserUsername(username, pageable);
        } else {
            postPage = postRepository.findAll(pageable);
        }
        return postPage.getContent();
    }

    @Transactional(readOnly = true)
    public List<Post> getTrendingPosts(int page, int size, int period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.minusDays(period);
        Timestamp timestamp = Timestamp.valueOf(startTime);

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findTrendingPosts(timestamp, pageable);

        return postPage.getContent();
    }

    @Transactional(readOnly = true)
    public List<Post> getSavedPosts(String username) {
        return postRepository.findByUserUsernameAndPublishStatus(username, false);
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

    public List<Series> getSeriesByUser(Long userId) {
        return seriesRepository.findByUserId(userId);
    }
}
