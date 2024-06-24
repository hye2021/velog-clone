package org.example.blog.controller;

import org.example.blog.entity.User;
import org.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PageController {
    @Autowired
    private UserService userService;
    @GetMapping("/@{username}")
    public String userBlog(@PathVariable String username,
                           RedirectAttributes redirectAttributes) {
        User user = userService.getUsersByUsername(username);
        if (user == null)
            return "redirect:/error";

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
        return "blog";
    }

}
