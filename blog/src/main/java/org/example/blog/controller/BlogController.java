package org.example.blog.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.example.blog.entity.User;
import org.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.example.blog.statics.Constants.*;

@Controller
public class BlogController {
    private final String PATH = "blog/";

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home() {
        return PATH + "home";
    }

    @GetMapping("/write")
    public String write() {
        return PATH + "write";
    }

    @GetMapping("/@{username}")
    public String userBlog(@PathVariable String username,
                           RedirectAttributes redirectAttributes) {
        User user = userService.getUsersByUsername(username);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "존재하지 않는 페이지입니다.");
            return "redirect:/error";
        }

        redirectAttributes.addFlashAttribute("user", user);
        return "redirect:/@" + username + "/" + "posts";
    }
    @GetMapping("/@{username}/{page}")
    public String userPage(@PathVariable String username,
                           @PathVariable String page,
                           Model model) {
        // Flash Attribute로 전달된 User 객체가 없다면 추가..
        if (!model.containsAttribute("user")) {
            User user = userService.getUsersByUsername(username);
            model.addAttribute("user", user);
        }
        model.addAttribute("page", page);
        return PATH + "blog";
    }
}
