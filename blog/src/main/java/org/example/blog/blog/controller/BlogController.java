package org.example.blog.blog.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.blog.user.entity.User;
import org.example.blog.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequiredArgsConstructor
public class BlogController {
    private final String PATH = "blog/";

    // dependency injection
    private final UserService userService;

// Home Page
    @GetMapping("/")
    public String home() {
        return "redirect:/recent";
    }

    @GetMapping("/{page}")
    public String homePage(@PathVariable("page") String page,
                           Model model) {
        model.addAttribute("page", page);
        return PATH + "home";
    }


// User Page
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

    @GetMapping("/error")
    public String error(Model model) {
        String errorMessage = (String) model.getAttribute("errorMessage");
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
        }
        return "error";
    }
}
