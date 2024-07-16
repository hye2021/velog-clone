package org.example.blog.post.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blog.post.entity.Post;
import org.example.blog.post.service.PostService;
import org.example.blog.security.dto.CustomUserDetails;
import org.example.blog.user.service.UserService;
import org.example.blog.statics.Constants;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final String PATH = "post/";

    // dependency injection
    private final PostService postService;

    @GetMapping("/write") // post editor page
    public String write(Model model) {
        model.addAttribute("post", new Post());
        return PATH + "write";
    }

    @GetMapping("/saves") // temporary saved posts list page
    public String getSavedPosts(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                Model model) {
        // 인증 정보가 없으면 spring security가 reject할 것임.
        String username = customUserDetails.getUsername();
        List<Post> posts = postService.getSavedPosts(username);
        model.addAttribute("posts", posts);
        return PATH + "save";
    }

    @GetMapping("/images/{username}/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable(name ="username", required = true) String username,
                                              @PathVariable(name ="filename", required = true) String filename) {
        try {
            Path path = Paths.get(Constants.IMAGE_PAHT + "/images/" + username + "/" + filename);
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok().body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
