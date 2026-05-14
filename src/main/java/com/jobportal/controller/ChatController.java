package com.jobportal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @PostMapping
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, String> request) {
        String message = request.getOrDefault("message", "").toLowerCase();
        String reply = "Sorry, please contact admin.";

        if (message.contains("hi") || message.contains("hello") || message.contains("hey")) {
            reply = "Hello! How can I help you today?";
        } else if (message.contains("register")) {
            reply = "You can register by clicking the 'Register' button on the top right corner. Choose whether you are a Student or Employer.";
        } else if (message.contains("apply for jobs") || message.contains("apply")) {
            reply = "To apply for jobs, login as a Student, browse the jobs list, and click on 'Apply'. Make sure your profile is complete.";
        } else if (message.contains("post jobs") || message.contains("post")) {
            reply = "To post jobs, login as an Employer, go to your dashboard, and click on 'Post a New Job'.";
        } else if (message.contains("login")) {
            reply = "Click the 'Login' button on the top right corner and enter your registered email and password.";
        } else if (message.contains("reset password") || message.contains("forgot")) {
            reply = "Go to the Login page and click 'Forgot Password?'. Enter your email to receive an OTP and set a new password.";
        } else if (message.contains("contact admin")) {
            reply = "You can reach the admin at admin@jobportal.com or call 1800-123-4567.";
        } else if (message.contains("latest jobs")) {
            reply = "You can view latest jobs on the Home page or login as a Student and visit the Jobs section.";
        } else if (message.contains("resume")) {
            reply = "You can upload or update your resume in your Profile section after logging in.";
        }

        Map<String, String> response = new HashMap<>();
        response.put("reply", reply);
        return ResponseEntity.ok(response);
    }
}
