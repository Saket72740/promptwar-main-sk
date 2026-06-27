package com.example.prompt_war_main.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        // Log the exception (stdout/logging)
        System.err.println("Uncaught Application Exception: " + ex.getMessage());
        ex.printStackTrace();

        // Pass a user-friendly message to the error page
        model.addAttribute("errorMessage", ex.getMessage() != null ? ex.getMessage() : "An unexpected wellness tracking error occurred.");
        return "error";
    }
}
