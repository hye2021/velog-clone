/*
package org.example.blog.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ModelAndView handleError(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("error");

        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        modelAndView.addObject("statusCode", statusCode);

        String errorMessage = getErrorMessage(statusCode);
        modelAndView.addObject("errorMessage", errorMessage);

        return modelAndView;
    }

    private String getErrorMessage(Integer statusCode) {
        if (statusCode == null) {
            return "Unknown error";
        }
        switch (statusCode) {
            case 400:
                return "Bad Request";
            case 401:
                return "Unauthorized";
            case 404:
                return "Page Not Found";
            case 500:
                return "Internal Server Error";
            default:
                return "Unexpected Error";
        }
    }
}
*/
