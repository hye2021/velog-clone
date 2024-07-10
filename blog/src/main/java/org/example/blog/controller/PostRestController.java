package org.example.blog.controller;

import lombok.RequiredArgsConstructor;
import org.example.blog.entity.Post;
import org.example.blog.entity.Series;
import org.example.blog.entity.Tag;
import org.example.blog.entity.User;
import org.example.blog.service.PostService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.example.blog.dto.PostDto;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.example.blog.service.UserService;

import java.sql.Timestamp;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostRestController {
    private final PostService postService;
    private final UserService userService;

    @PostMapping
    public PostDto createPost(@ModelAttribute PostDto postDto,
                              RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        // User
        String username = authentication.getName();
        User user = userService.getUsersByUsername(username);

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
            Series series = new Series();
            series.setTitle(postDto.getNewSeriesTitle());
            series.setUser(user);
            postService.saveSeries(series);
            post.setSeries(series);
        }

        // tag 설정
        if (postDto.getTags() != null && !postDto.getTags().isEmpty()) {
            String[] tags = postDto.getTags().split(" ");
            for (String tag : tags) {
                if (!tag.startsWith("#"))
                    continue;
                Tag newTag = new Tag();
                newTag.setName(tag.substring(1)); // # 제외
                newTag.setUser(user);
                post.getTags().add(newTag);
            }
        }

        // Thumbnail 저장
        MultipartFile thumbnail = postDto.getThumbnail();
        if (thumbnail != null && !thumbnail.isEmpty()) {
            String thumbnailPath = postService.saveThumbnailImg(thumbnail);
            post.setThumbnailPath(thumbnailPath);
        }

        // Post 저장
        postService.savePost(post);
        return postDto;
    }
}
