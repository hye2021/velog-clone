package org.example.blog.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("currentUser")
    public String currentUser() {
        return UserContext.getCurrentUser();
    }

    @ModelAttribute("isLogin")
    public boolean isLogin() {
        return UserContext.getCurrentUser() != null;
    }
}
