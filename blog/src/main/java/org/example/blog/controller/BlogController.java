package org.example.blog.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.example.blog.entity.User;
import org.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static org.example.blog.statics.Constants.*;

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
                        Model model,
                        HttpServletResponse response) {
        User user = userService.getUsersByUsername(username);
        if (user == null || !userService.checkPassword(user, password)) {
            model.addAttribute("error", "잘못된 입력!!!!!!!!!");
            return "loginform";
        }

        // [로그인 성공] 로그인 정보 쿠키 등록
        Cookie cookie = new Cookie(COOKIE_USER, user.getId().toString());
        cookie.setPath("/"); // 모든 경로에서 쿠키 접근 가능
        cookie.setHttpOnly(true); // 자바스크립트에서 쿠키 접근 불가 -> document.cookie로 확인 불가 -> 쿠키를 통한 XSS 공격 방지
        cookie.setMaxAge(60 * 5); // 5분 동안 유지
        response.addCookie(cookie);

        /* HttpServletResponse 객체
        *
        * 클라이언트에게 응답을 생성하는 데 사용된다.
        * Spring MVC에서는 컨트롤러 메서드의 파라미터로 선언하면 자동으로 주입된다.
        * 개발자가 응답을 커스터마이징 할 수 있다.
        * 이 객체는 요청 처리 중에 자동으로 주입된다. by Spring MVC
        *
        * 역할 1. 클라이언트에게 전송할 응답 데이터를 설정한다.
        * 역할 2. HTTP 응답 헤더를 설정한다.
        * 역할 3. 응답에 포함될 쿠키를 설정한다.
        * 역할 4. HTTP 상태 코드를 설정한다.
        */

        return "redirect:/@" + username;
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
}
