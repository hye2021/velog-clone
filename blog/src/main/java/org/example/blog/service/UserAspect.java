package org.example.blog.service;

import org.example.blog.annotation.CheckLogin;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.blog.config.UserContext;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class UserAspect {
    @Before("@annotation(checkLogin)")
    public void checkLoginStatus(CheckLogin checkLogin) {
        String currentUser = UserContext.getCurrentUser();
        if (currentUser == null) {
            // return 혹은 throw 처리
        }
        // 성공시 처리
    }
}
