package org.example.blog.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
import com.google.gson.Gson;
import java.util.HashMap;

/*
 * 인증 진입점 필터 (AuthenticationEntryPoint)
 * 사용자의 인증 요청에 대한 응답을 처리한다.
 * 주로 JSON 형식의 오류 메시지를 생성하여 클라이언트에 반환하거나,
 * 페이지 요청일 경우 리다이렉트를 처리한다.
 * commence() : 요청에서 발생한 인증 예외를 처리한다. (인증되지 않은 사용자)
 */

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // 사용자가 인증되지 않았을 때 어떻게 처리할지를 구현함
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // 예외 메시지를 받아옴
        String exceptin = (String)request.getAttribute("exception");

        //어떤요청인지를 구분..
        //RESTful로 요청한건지..  그냥 페이지 요청한건지 구분해서 다르게 동작하도록 구현.
        if(isRestRequest(request)){
            handleRestResponse(request,response,exceptin);
        }else{
            handlePageResponse(request,response,exceptin);
        }
    }

    private boolean isRestRequest(HttpServletRequest request) {
        String requestedWithHeader = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestedWithHeader) || request.getRequestURI().startsWith("/api/");
    }

    //페이지로 요청이 들어왔을 때 인증되지 않은 사용자라면 무조건 /loginform으로 리디렉션 시키겠다.
    private void handlePageResponse(HttpServletRequest request, HttpServletResponse response, String exception) throws IOException {
        log.error("Page Request - Commence Get Exception : {}", exception);

        if (exception != null) {
            // 추가적인 페이지 요청에 대한 예외 처리 로직을 여기에 추가할 수 있습니다.
        }

        response.sendRedirect("/loginform");
    }

    // RESTful로 요청이 들어왔을 때 인증되지 않은 사용자라면 예외 코드에 따라 응답을 설정
    private void handleRestResponse(HttpServletRequest request, HttpServletResponse response, String exception) throws IOException {
        log.error("Rest Request - Commence Get Exception : {}", exception);

        if (exception != null) {
            if (exception.equals(JwtExceptionCode.INVALID_TOKEN.getCode())) {
                log.error("entry point >> invalid token");
                setResponse(response, JwtExceptionCode.INVALID_TOKEN);
            } else if (exception.equals(JwtExceptionCode.EXPIRED_TOKEN.getCode())) {
                log.error("entry point >> expired token");
                setResponse(response, JwtExceptionCode.EXPIRED_TOKEN);
            } else if (exception.equals(JwtExceptionCode.UNSUPPORTED_TOKEN.getCode())) {
                log.error("entry point >> unsupported token");
                setResponse(response, JwtExceptionCode.UNSUPPORTED_TOKEN);
            } else if (exception.equals(JwtExceptionCode.NOT_FOUND_TOKEN.getCode())) {
                log.error("entry point >> not found token");
                setResponse(response, JwtExceptionCode.NOT_FOUND_TOKEN);
            } else {
                setResponse(response, JwtExceptionCode.UNKNOWN_ERROR);
            }
        } else {
            setResponse(response, JwtExceptionCode.UNKNOWN_ERROR);
        }
    }

    // 예외 코드에 따라 응답을 설정
    private void setResponse(HttpServletResponse response, JwtExceptionCode exceptionCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        HashMap<String, Object> errorInfo = new HashMap<>();
        errorInfo.put("message", exceptionCode.getMessage());
        errorInfo.put("code", exceptionCode.getCode());
        Gson gson = new Gson();
        String responseJson = gson.toJson(errorInfo);
        response.getWriter().print(responseJson);
    }

}