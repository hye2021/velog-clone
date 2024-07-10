package org.example.blog.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blog.entity.Post;
import org.example.blog.entity.Series;
import org.example.blog.entity.Tag;
import org.example.blog.entity.User;
import org.example.blog.service.PostService;
import org.example.blog.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @PostMapping("/upload") // upload post
    public String upload(@ModelAttribute(name = "post") Post post,
                         @RequestParam(name = "str_tags", required = false, defaultValue = "") String strTags,
                         @RequestParam(name = "str_series", required = false, defaultValue = "") String newSeries,
                         RedirectAttributes redirectAttributes) {
        return "redirect:/";
    }
}
