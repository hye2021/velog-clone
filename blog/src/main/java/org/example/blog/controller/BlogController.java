package org.example.blog.controller;

import lombok.RequiredArgsConstructor;
import org.example.blog.entity.User;
import org.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BlogController {
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/loginform")
    public String loginform(Model model) {
        return "loginform";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        Model model) {
        User user = userService.getUsersByUsername(username);
        if (user == null || !userService.checkPassword(user, password)) {
            model.addAttribute("error", "잘못된 입력!!!!!!!!!");
            return "loginform";
        }

        return "redirect:/@" + username;
    }

    @GetMapping("/@{username}")
    public String userblog(@PathVariable String username,
                           Model model) {
        User user = userService.getUsersByUsername(username);
        if (user == null) {
            return "redirect:/error";
        }

        model.addAttribute("user", user);
        return "blog";
    }

    @GetMapping("/userregform")
    public String userregform(Model model) {
        model.addAttribute("user", new User());
        return "userregform";
    }

    @PostMapping("/userreg")
    public String userreg(@ModelAttribute User user) {
        // todo: 오류 페이지 redirect
        userService.createUser(user);

        return "redirect:/welcome";
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }
}
