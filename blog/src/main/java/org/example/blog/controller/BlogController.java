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
    @Autowired
    private UserService userService;

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/loginform")
    public String loginform(Model model) {
        return "user/loginform";
    }


    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        // 쿠키 삭제
        Cookie cookie = new Cookie(COOKIE_USER, null);
        cookie.setMaxAge(0); // 쿠키의 만료 시간을 0으로 설정하여 삭제
        cookie.setPath("/"); // 쿠키의 경로 설정
        response.addCookie(cookie);

        return "redirect:/";
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

    @GetMapping("/write")
    public String write() {
        return "write";
    }

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
