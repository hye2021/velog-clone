package org.example.blog.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/***
 * GlobalControllerAdvice
 * 모든 컨트롤러 요청에 대해 속성을 자동으로 모델에 추가한다.
 * 따라서 각 컨드롤러에서 별도로 추가 작업을 할 필요가 없다.
 */

@ControllerAdvice
public class GlobalControllerAdvice {
    @ModelAttribute("isLogin")
    public boolean isLogin() {
        return UserContext.isLogin();
    }
}
