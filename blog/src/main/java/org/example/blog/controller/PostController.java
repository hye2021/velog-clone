package org.example.blog.controller;

import lombok.RequiredArgsConstructor;
import org.example.blog.service.BlogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final String PATH = "post/";

    // dependency injection
    private final BlogService blogService;

    @GetMapping("/write") // post editor page
    public String write() {
        return PATH + "write";
    }

    @PostMapping("/upload") // upload post
    public String upload() {
        return "redirect:/";
    }
}
