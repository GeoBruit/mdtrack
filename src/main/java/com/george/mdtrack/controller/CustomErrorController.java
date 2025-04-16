package com.george.mdtrack.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller handles errors for the application.
 * It maps requests to "/error" and returns custom error pages
 * based on the HTTP status code.
 */
@Controller
@RequestMapping("/error")
public class CustomErrorController {

    /**
     * This method handles GET requests to "/error".
     * It checks the status code set by the servlet container and
     * returns a custom view name for specific error codes.
     *
     * @param request the HTTP request, used to extract the error status code
     * @param model the model to pass attributes to the view (not used here but included for flexibility)
     * @return a string representing the name of the error view
     */
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
