package org.example.blog.post.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blog.post.entity.Post;
import org.example.blog.post.service.PostService;
import org.example.blog.user.service.UserService;
import org.example.blog.statics.Constants;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final String PATH = "post/";

    // dependency injection
    private final PostService postService;
    private final UserService userService;

    @GetMapping("/write") // post editor page
    public String write(Model model) {
        model.addAttribute("post", new Post());
        return PATH + "write";
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
