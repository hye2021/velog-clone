package org.example.blog.post.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blog.post.entity.Post;
import org.example.blog.post.entity.Series;
import org.example.blog.post.entity.Tag;
import org.example.blog.user.entity.User;
import org.example.blog.post.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.example.blog.post.dto.PostDto;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.example.blog.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostRestController {
    private final PostService postService;

    @PostMapping
    public PostDto createPost(@ModelAttribute PostDto postDto,
                              RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }

        // 필수 요소 : title
        if (postDto.getTitle() == null || postDto.getTitle().trim().isEmpty()) {
            return null;
        }

        // User
        String username = authentication.getName();
        User user = postService.getUsersByUsername(username);

        // Post 기본 설정
        Post post = new Post();
        post.setUser(user);
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setPublishStatus(postDto.isPublishStatus());

        // post 시리즈 설정
        if (postDto.getSeriesId() != null) {
            Series series = postService.getSeriesById(postDto.getSeriesId());
            post.setSeries(series);
        } else if (postDto.getNewSeriesTitle() != null) {
            log.info("      new series title: " + postDto.getNewSeriesTitle());
            // 만약에 title이 공백밖에 없다면 -> 대체 왜이러는지는 모르겠다만..
            if (postDto.getNewSeriesTitle().trim().isEmpty())
                postDto.setNewSeriesTitle(null);
            else {
                Series series = new Series();
                series.setTitle(postDto.getNewSeriesTitle());
                series.setUser(user);
                postService.saveSeries(series);
                post.setSeries(series);
            }
        }

        // tag 설정
        if (postDto.getTags() != null && !postDto.getTags().isEmpty()) {
            String[] tags = postDto.getTags().split(" ");
            for (String tag : tags) {
                if (!tag.startsWith("#"))
                    continue;
                Tag newTag = postService.saveTag(tag, user);
                post.getTags().add(newTag);
            }
        }

        // 썸네일 경로
        if (postDto.getThumbnail() != null && !postDto.getThumbnail().isEmpty()) {
            post.setThumbnailPath(postDto.getThumbnail());
        }

        // Post 저장
        postService.savePost(post);
        return postDto;
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Post>> getRecentPosts(@RequestParam(value = "page", defaultValue = "0") int page,
                                                     @RequestParam(value = "size", defaultValue = "10") int size,
                                                     @RequestParam(value = "username", defaultValue = "", required = false) String username) {
        List<Post> posts = postService.getRecentPosts(page, size, username);
        if(posts == null)
            return ResponseEntity.badRequest().body(null);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/trending")
    public ResponseEntity<List<Post>> getTrendingPosts(@RequestParam(value = "page", defaultValue = "0") int page,
                                                       @RequestParam(value = "size", defaultValue = "10") int size,
                                                       @RequestParam(value = "period", defaultValue = "7", required = false) int period) {
        List<Post> posts = postService.getTrendingPosts(page, size, period);
        if(posts == null)
            return ResponseEntity.badRequest().body(null);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/image")
    public ResponseEntity<String> handleImageUpload(@RequestParam("file") MultipartFile file) {
        log.info("*** [upload image file] : " + file.getOriginalFilename());

        if (file.isEmpty())
            return ResponseEntity.badRequest().body("empty file");

        // 현재 user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.badRequest().body("Failed to get current user");
        }
        String username = authentication.getName();

        // 파일 업로드
        try {
            String thumbnailPath = postService.saveImage(file, username);
            return ResponseEntity.ok().body(thumbnailPath);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to save a image");
        }
    }

    @GetMapping("/{username}/series")
    public ResponseEntity<List<Series>> getSeriesByUserUsername(@PathVariable(name = "username") String username) {
        User user = postService.getUsersByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Series> series = postService.getSeriesByUser(user.getId());
        return new ResponseEntity<>(series, HttpStatus.OK);
    }

}
