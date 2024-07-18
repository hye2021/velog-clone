package org.example.blog.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blog.post.dto.PostCardDto;
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

/* post */
    @Transactional(readOnly = true)
    public Post getPostById(Long postId) {
        return postRepository.findByIdAndPublishStatus(postId, true);
    }

    /* recent */
    private Pageable createRecentPageable(int page, int size) {
        return PageRequest.of(page, size, Sort.by("updateTime").descending()
                .and(Sort.by("id").descending()));
    }

    @Transactional(readOnly = true)
    public Page<Post> getRecentPosts(int page, int size, String username, String tagname) {
        if (tagname == null || tagname.isEmpty()) {
            if (username == null || username.isEmpty()) {
//                  log.info("*** [getRecentPosts] username: null, tagname: null");
                return getAllRecentPosts(page, size);
            }
            else {
                 log.info("*** [getRecentPosts] username: " + username + ", tagname: null");
                return getRecentPostsByUsername(page, size, username);
            }
        } else {
            if (username == null || username.isEmpty()) {
//                 log.info("*** [getRecentPosts] username: null, tagname: " + tagname);
                return getRecentPostsByTag(page, size, tagname);
            } else {
//                 log.info("*** [getRecentPosts] username: " + username + ", tagname: " + tagname);
                return getRecentPostsByUsernameAndTags(page, size, username, tagname);
            }
        }
    }

    @Transactional(readOnly = true)
    public Page<Post> getAllRecentPosts(int page, int size) {
        Pageable pageable = createRecentPageable(page, size);
        Page<Post> postPage = postRepository.findByPublishStatus(true, pageable);
        return postPage;
    }


    @Transactional(readOnly = true)
    public Page<Post> getRecentPostsByUsername(int page, int size, String username) {
        Pageable pageable = createRecentPageable(page, size);
        Page<Post> postPage = postRepository.findByUserUsernameAndPublishStatus(username, true, pageable);
        return postPage;
    }

    @Transactional(readOnly = true)
    public Page<Post> getRecentPostsByTag(int page, int size, String tag) {
        Pageable pageable = createRecentPageable(page, size);
        Page<Post> postPage =  postRepository.findByTagsNameAndPublishStatus(tag, true, pageable);
        return postPage;
    }

    @Transactional(readOnly = true)
    public Page<Post> getRecentPostsByUsernameAndTags(int page, int size, String username, String tagname) {
        Pageable pageable = createRecentPageable(page, size);
        Page<Post> postPage = postRepository.findByUserUsernameAndTagsNameAndPublishStatus(username, tagname, true, pageable);
        return postPage;
    }

    /* trending... */
    @Transactional(readOnly = true)
    public Page<Post> getTrendingPosts(int page, int size, int period) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.minusDays(period);
        Timestamp timestamp = Timestamp.valueOf(startTime);

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findTrendingPosts(timestamp, pageable);

        return postPage;
    }

    public Page<PostCardDto> getPostCardDtos(Page<Post> posts) {
        return posts.map(PostCardDto::new);
    }

    @Transactional(readOnly = true)
    public List<Post> getSavedPosts(String username) {
        return postRepository.findByUserUsernameAndPublishStatus(username, false);
    }

    @Transactional
    public void deletePost(Post post, String username) {
        if (!post.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You are not authorized to delete this post");
        }
        postRepository.delete(post);
    }

    @Transactional
    public Post savePost(Post post) {
        post.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        return postRepository.save(post);
    }

/* image */
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

/* tag */
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

    @Transactional(readOnly = true)
    public List<Tag> getAllTagsByUser(Long userId) {
        return tagRepository.findAllByUserId(userId);
    }

/* series */
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
