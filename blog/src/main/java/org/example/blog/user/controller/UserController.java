package org.example.blog.user.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
public class UserController {
    private final String PATH = "user/";

    @Autowired private UserService userService;
    @Autowired private RefreshTokenService refreshTokenService;

    @GetMapping("/loginform")
    public String loginform(Model model) {
        return PATH + "loginform";
    }


    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response) {
        // 현재 사용자 인증 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Long userId = userService.getUsersByUsername(username).getId();

            // SecurityContextHolder에서 사용자 인증 정보 제거
            SecurityContextHolder.clearContext();

            // 쿠키 제거
            Cookie accessTokenCookie = new Cookie(ACCESS_TOKEN, null);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(0);

            Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN, null);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(0);

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            // 데이터베이스에서 Refresh Token 제거
            refreshTokenService.deleteRefreshTokenByUserId(userId);
        }

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
