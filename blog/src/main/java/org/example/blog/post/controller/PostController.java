package org.example.blog.post.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blog.post.entity.Post;
import org.example.blog.post.service.PostService;
import org.example.blog.security.dto.CustomUserDetails;
import org.example.blog.user.entity.User;
import org.example.blog.statics.Constants;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

//    @GetMapping("/{username}/{page}")
//    public String getPost(@PathVariable(name = "username") String username,
//                          @PathVariable(name = "page") String page,
//                          Model model,
//                          RedirectAttributes redirectAttributes) {
////        // user 페이지에서 posts, series, about 페이지로 이동하는 경우
////        if (UserPageEnums.contains(page)) {
////            User user = postService.getUsersByUsername(username);
////            if(user == null) {
////                redirectAttributes.addFlashAttribute("message", "존재하지 않는 페이지입니다.");
////                return "redirect:/error";
////            }
////            model.addAttribute("user", user);
////            model.addAttribute("page", page);
////            return "blog/blog";
////        }
//
//        // user 페이지에서 post 페이지로 이동하는 경우
//        Long postId;
//        try {
//            postId = Long.parseLong(page);
//        } catch (NumberFormatException e) {
//            redirectAttributes.addFlashAttribute("message", "존재하지 않는 페이지입니다.");
//            return "redirect:/error";
//        }
//
//        Post post = postService.getPostById(postId);
//        if (post == null) {
//            redirectAttributes.addFlashAttribute("message", "존재하지 않는 페이지입니다.");
//            return "redirect:/error";
//        }
//        model.addAttribute("post", post);
//        return PATH + "post";
//    }

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
