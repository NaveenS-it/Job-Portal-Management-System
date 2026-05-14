package com.jobportal.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException exc, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "File too large! Must be under 10MB.");
        // We'll redirect to a generic error page, or maybe root. Let's redirect to back previous page.
        // Spring's referer header can be used, but for simplicity, we redirect to dashboard
        return "redirect:/"; // This should ideally be back to where they came from
    }
    
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        System.err.println("GLOBAL EXCEPTION HANDLER CAUGHT:");
        e.printStackTrace();
        model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
        return "error";
    }
}
