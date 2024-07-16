package org.example.blog.user.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.example.blog.user.entity.User;
import org.example.blog.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.example.blog.statics.Constants.COOKIE_USER;

@Controller
public class UserController {
    private final String PATH = "user/";

    @Autowired
    private UserService userService;

    @GetMapping("/loginform")
    public String loginform(Model model) {
        return PATH + "loginform";
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
