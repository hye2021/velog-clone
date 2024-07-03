package org.example.blog.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import org.example.blog.dto.UserLoginDto;
import org.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

import static org.example.blog.statics.Constants.COOKIE_USER;

@RestController
@RequestMapping("/api/users")
public class UserRestController {
    @Autowired
    private UserService userService;

    @GetMapping("/check-username")
    public Map<String, Boolean> checkUsername(String username) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", userService.checkUsername(username));
        return response;
    }

    @GetMapping("/check-email")
    public Map<String, Boolean> checkEmail(String email) {
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", userService.checkEmail(email));
        return response;
    }
}
