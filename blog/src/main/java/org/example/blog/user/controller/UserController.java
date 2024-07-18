package org.example.blog.user.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.blog.user.entity.User;
import org.example.blog.user.service.RefreshTokenService;
import org.example.blog.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.example.blog.statics.Constants.*;

@Controller
@Slf4j
public class UserController {
    private final String PATH = "user/";

    @Autowired private UserService userService;
    @Autowired private RefreshTokenService refreshTokenService;

    @GetMapping("/loginform")
    public String loginform(Model model) {
        return PATH + "loginform";
    }

    @GetMapping("/userregform")
    public String userregform(Model model) {
        model.addAttribute("user", new User());
        return PATH + "userregform";
    }

    @PostMapping("/userreg")
    public String userreg(@ModelAttribute User user,
                          RedirectAttributes redirectAttributes) {
        // todo: 오류 페이지 redirect
        try {
            userService.createUser(user);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage" , e);
            return "redirect:/error";
        }

        return "redirect:/welcome";
    }

    @GetMapping("/welcome")
    public String welcome() {
        return PATH + "welcome";
    }
}
