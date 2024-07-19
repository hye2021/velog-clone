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

    @GetMapping("/@{username}/posts")
    public String posts(@PathVariable("username") String username,
                           Model model) {
        return gotoUserBlog(username, "posts", model, null);
    }

    @GetMapping("/@{username}/series")
    public String series(@PathVariable("username") String username,
                           Model model) {
        return gotoUserBlog(username, "series", model, null);
    }

    @GetMapping("/@{username}/about")
    public String about(@PathVariable("username") String username,
                           Model model) {
        return gotoUserBlog(username, "about", model, null);
    }

    private String gotoUserBlog(String username,
                                String page,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        User user = userService.getUsersByUsername(username);
        if(user == null) {
            redirectAttributes.addFlashAttribute("message", "존재하지 않는 페이지입니다.");
            return "redirect:/error";
        }
        model.addAttribute("user", user);
        model.addAttribute("username", username);
        model.addAttribute("page", page);
        return "blog/blog";
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
