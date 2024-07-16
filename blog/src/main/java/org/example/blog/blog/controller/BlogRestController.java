package org.example.blog.blog.controller;

import lombok.RequiredArgsConstructor;
import org.example.blog.blog.service.BlogService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogRestController {
    // dependency injection
    private final BlogService blogService;

}
