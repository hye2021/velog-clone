package org.example.blog.controller;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.blog.security.dto.UserLoginDto;
import org.example.blog.security.dto.UserLoginResponseDto;
import org.example.blog.entity.RefreshToken;
import org.example.blog.entity.Role;
import org.example.blog.security.jwt.util.JwtTokenizer;
import org.example.blog.service.RefreshTokenService;
import org.example.blog.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.example.blog.entity.User;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BlogRestController {

    private final UserService userService;


}
