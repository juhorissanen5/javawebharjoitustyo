package com.example.javawebharjoitustyo;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String errorMessage = "An error occurred";
        
        if (statusCode == null) {
            statusCode = 500;
        }
        
        switch(statusCode) {
            case 404:
                errorMessage = "Page not found";
                break;
            case 403:
                errorMessage = "Access denied";
                break;
            case 500:
                errorMessage = "Internal server error";
                break;
            default:
                errorMessage = "Unexpected error";
        }
        
        model.addAttribute("statusCode", statusCode);
        model.addAttribute("errorMessage", errorMessage);
        return "error.html";  // specify the full template name
    }
}