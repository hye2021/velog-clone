package org.example.blog.post.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blog.post.dto.PostCardDto;
import org.example.blog.post.entity.Post;
import org.example.blog.post.entity.Series;
import org.example.blog.post.entity.Tag;
import org.example.blog.user.entity.User;
import org.example.blog.post.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.example.blog.post.dto.PostCreateDto;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostRestController {
    private final PostService postService;

    @PostMapping
    public PostCreateDto createPost(@ModelAttribute PostCreateDto postCreateDto,
                                    RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }

        // 필수 요소 : title
        if (postCreateDto.getTitle() == null || postCreateDto.getTitle().trim().isEmpty()) {
            return null;
        }

        // User
        String username = authentication.getName();
        User user = postService.getUsersByUsername(username);

        // Post 기본 설정
        Post post = new Post();
        post.setUser(user);
        post.setTitle(postCreateDto.getTitle());
        post.setContent(postCreateDto.getContent());
        post.setPublishStatus(postCreateDto.isPublishStatus());

        // post 시리즈 설정
        if (postCreateDto.getSeriesId() != null) {
            Series series = postService.getSeriesById(postCreateDto.getSeriesId());
            post.setSeries(series);
        } else if (postCreateDto.getNewSeriesTitle() != null) {
            log.info("      new series title: " + postCreateDto.getNewSeriesTitle());
            // 만약에 title이 공백밖에 없다면 -> 대체 왜이러는지는 모르겠다만..
            if (postCreateDto.getNewSeriesTitle().trim().isEmpty())
                postCreateDto.setNewSeriesTitle(null);
            else {
                Series series = new Series();
                series.setTitle(postCreateDto.getNewSeriesTitle());
                series.setUser(user);
                postService.saveSeries(series);
                post.setSeries(series);
            }
        }

        // tag 설정
        if (postCreateDto.getTags() != null && !postCreateDto.getTags().isEmpty()) {
            String[] tags = postCreateDto.getTags().split(" ");
            for (String tag : tags) {
                if (!tag.startsWith("#"))
                    continue;
                Tag newTag = postService.saveTag(tag, user);
                post.getTags().add(newTag);
            }
        }

        // 썸네일 경로
        if (postCreateDto.getThumbnail() != null && !postCreateDto.getThumbnail().isEmpty()) {
            post.setThumbnailPath(postCreateDto.getThumbnail());
        }

        // Post 저장
        postService.savePost(post);
        return postCreateDto;
    }

    @DeleteMapping
    public ResponseEntity<String> deletePost(@RequestParam("postId") Long postId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.badRequest().body("Failed to get current user");
        }
        String username = authentication.getName();
        Post post = postService.getPostById(postId);
        if (post == null) {
            return ResponseEntity.badRequest().body("Failed to get post");
        }

        try {
            postService.deletePost(post, username);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete post");
        }

        return ResponseEntity.ok().body("Post deleted successfully");
    }

    @GetMapping("/recent")
    public ResponseEntity<Page<PostCardDto>> getRecentPosts(@RequestParam(value = "page", defaultValue = "0") int page,
                                                     @RequestParam(value = "size", defaultValue = "10") int size,
                                                     @RequestParam(value = "username", defaultValue = "", required = false) String username,
                                                     @RequestParam(value = "tag", defaultValue = "", required = false) String tag,
                                                     @RequestParam(value = "seriesId", defaultValue = "", required = false) Long seriesId) {
        Page<Post> posts = postService.getRecentPosts(page, size, username, tag, seriesId);
        if(posts == null) {
            return ResponseEntity.noContent().build();
        }
        Page<PostCardDto> postCardDtos = postService.getPostCardDtos(posts);

        return ResponseEntity.ok(postCardDtos);
    }

    @GetMapping("/trending")
    public ResponseEntity<Page<PostCardDto>> getTrendingPosts(@RequestParam(value = "page", defaultValue = "0") int page,
                                                       @RequestParam(value = "size", defaultValue = "10") int size,
                                                       @RequestParam(value = "period", defaultValue = "7", required = false) int period) {
        Page<Post> posts = postService.getTrendingPosts(page, size, period);
        if(posts == null)
            return ResponseEntity.badRequest().body(null);

        Page<PostCardDto> postCardDtos = postService.getPostCardDtos(posts);
        return ResponseEntity.ok(postCardDtos);
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
        } catch (MaxUploadSizeExceededException e) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("File too large");
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

    @GetMapping("/{username}/tags")
    public ResponseEntity<Map<String, List<String>>> getTagsByUserUsername(@PathVariable(name = "username") String username) {
        User user = postService.getUsersByUsername(username);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<String> tags = new ArrayList<>();
        for (Tag tag : postService.getAllTagsByUser(user.getId())) {
            tags.add(tag.getName());
        }

        Map<String, List<String>> response = new HashMap<>();
        response.put("content", tags);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
