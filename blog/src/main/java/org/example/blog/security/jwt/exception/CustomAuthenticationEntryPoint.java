package org.example.blog.security.jwt.exception;

import jakarta.servlet.FilterChain;
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

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // 사용자가 인증되지 않았을 때 어떻게 처리할지를 구현함
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        log.info("*** CustomAuthenticationEntryPoint >> commence");

        // 예외 메시지를 받아옴
        String exception = (String)request.getAttribute("exception");
        log.info("*** exception : {}", exception);
        log.info("*** request.getRequestURI() : {}", request.getRequestURI());
        log.info("*** request : {}", request);

        // todo: exception 이 없으면
        if (exception == null) {
            FilterChain filterChain = (FilterChain) request.getAttribute("filterChain");
            if (filterChain != null) {
                filterChain.doFilter(request, response);
            }
        }

        //어떤요청인지를 구분..
        //RESTful로 요청한건지..  그냥 페이지 요청한건지 구분해서 다르게 동작하도록 구현.
        if(isRestRequest(request)){
            log.info("*** Rest Request");
            handleRestResponse(request,response,exception);
        }else{
            log.info("*** Page Request");
            handlePageResponse(request,response,exception);
        }
    }

    private boolean isRestRequest(HttpServletRequest request) {
        String requestedWithHeader = request.getHeader("X-Requested-With");
        log.info("*** requestedWithHeader : {}", requestedWithHeader);
        return "XMLHttpRequest".equals(requestedWithHeader);
    }

    //페이지로 요청이 들어왔을 때 인증되지 않은 사용자라면 무조건 /loginform으로 리디렉션 시키겠다.
    private void handlePageResponse(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String exception) throws IOException {
        log.error("[Authentication Entry Point] Page Request - Commence Get Exception : {}", exception);

        if (exception != null) {
            // 추가적인 페이지 요청에 대한 예외 처리 로직을 여기에 추가할 수 있습니다.
        }

        response.sendRedirect("/loginform");
    }

    // RESTful로 요청이 들어왔을 때 인증되지 않은 사용자라면 예외 코드에 따라 응답을 설정
    private void handleRestResponse(HttpServletRequest request,
                                    HttpServletResponse response,
                                    String exception) throws IOException {
        log.error("[Authentication Entry Point] Rest Request - Commence Get Exception : {}", exception);

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
                log.error("entry point >> unknown error");
                setResponse(response, JwtExceptionCode.UNKNOWN_ERROR);
            }
        } else {
            // 예외 코드가 없을 경우
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
