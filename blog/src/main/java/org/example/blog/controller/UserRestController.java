package org.example.blog.controller;

import lombok.NoArgsConstructor;
import org.example.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api")
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
