package org.example.blog.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.blog.statics.UserContext;
import org.springframework.stereotype.Component;


//
// 일단 지금은 Controller에서 UserContext를 통해 직접 확인하고
// 나중에 로그인 확인하는 처리가 많아지거나 복잡해 지면
// AOP로 처리하는 방법으로 바꾸자
//


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
