package org.example.blog.controller;

import lombok.RequiredArgsConstructor;
import org.example.blog.entity.User;
import org.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class BlogController {
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String blog() {
        return "blog";
    }

    @GetMapping("/loginform")
    public String loginform() {
        return "loginform";
    }

    @GetMapping("/userregform")
    public String userregform(Model model) {
        model.addAttribute("user", new User());
        return "userregform";
    }

    @PostMapping("/userreg")
    public String userreg(@ModelAttribute User user,
                          RedirectAttributes redirectAttributes) {
        userService.createUser(user);
        redirectAttributes.addFlashAttribute("message", "User registered successfully");
        return "redirect:/welcome";
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }
}
