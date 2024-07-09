package org.example.blog.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blog.entity.User;
import org.example.blog.service.BlogService;
import org.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.example.blog.statics.Constants.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class BlogController {
    private final String PATH = "blog/";

    // dependency injection
    private final UserService userService;

    @GetMapping("/")
    public String home() {
        return PATH + "home";
    }

    @GetMapping("/error")
    public String error(Model model) {
        String errorMessage = (String) model.getAttribute("errorMessage");
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
        }
        return "error";
    }

    @GetMapping("/@{username}")
    public String userBlog(@PathVariable("username") String username,
                           RedirectAttributes redirectAttributes) {
        log.info("*** username: {}", username);
        User user = userService.getUsersByUsername(username);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "존재하지 않는 페이지입니다.");
            return "redirect:/error";
        }

        redirectAttributes.addFlashAttribute("user", user);
        return "redirect:/@" + username + "/" + "posts";
    }
    @GetMapping("/@{username}/{page}")
    public String userPage(@PathVariable("username") String username,
                           @PathVariable("page") String page,
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
