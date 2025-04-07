package com.george.mdtrack.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class CustomErrorController {

    @GetMapping
    public String handleError(HttpServletRequest request, Model model) {
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (statusObj != null) {
            int statusCode = Integer.parseInt(statusObj.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error/404";
            } else {
                return "error/500";
            }
        }
        return "error/500";
    }
}
