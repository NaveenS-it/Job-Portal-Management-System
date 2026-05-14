package com.jobportal.controller;

import com.jobportal.entity.User;
import com.jobportal.service.EmailService;
import com.jobportal.service.OtpService;
import com.jobportal.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, 
                               BindingResult result, 
                               @RequestParam("role") String role, 
                               Model model,
                               HttpSession session) {
        
        if (result.hasErrors()) {
            return "register";
        }

        if (userService.findByEmail(user.getEmail()) != null) {
            model.addAttribute("emailError", "Email already registered! Each email can only be linked to one role (Job Seeker or Employer). Please login or use a different email.");
            return "register";
        }

        userService.registerUser(user, role);
        
        // Generate and send OTP
        otpService.generateAndSendOtp(user.getEmail(), "REGISTER");
        session.setAttribute("otpEmail", user.getEmail());
        session.setAttribute("otpPurpose", "REGISTER");
        
        // Send registration email
        emailService.sendEmail(user.getEmail(), "Welcome to Job Portal", "You have successfully registered. Please verify your email using the OTP sent to you.");

        return "redirect:/verify-otp";
    }

    @GetMapping("/verify-otp")
    public String showVerifyOtpPage(HttpSession session, Model model) {
        String email = (String) session.getAttribute("otpEmail");
        if (email == null) {
            return "redirect:/register";
        }
        model.addAttribute("email", email);
        return "verify-otp";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") String otp, HttpSession session, Model model) {
        String email = (String) session.getAttribute("otpEmail");
        if (email == null) {
            return "redirect:/register";
        }

        if (otpService.verifyOtp(email, otp)) {
            userService.activateUser(email);
            session.removeAttribute("otpEmail");
            
            // Notification for verification
            emailService.sendEmail(email, "Account Activated", "Your account has been successfully verified. You can now login.");
            
            String isForgotPassword = (String) session.getAttribute("forgotPassword");
            if (isForgotPassword != null && isForgotPassword.equals("true")) {
                session.setAttribute("resetEmail", email);
                return "redirect:/reset-password";
            }
            
            return "redirect:/login?verified=true";
        } else {
            model.addAttribute("error", "Invalid or expired OTP");
            model.addAttribute("email", email);
            return "verify-otp";
        }
    }

    @GetMapping("/verify-login-otp")
    public String showVerifyLoginOtpPage(HttpSession session, Model model) {
        String email = (String) session.getAttribute("otpEmail");
        if (email == null) return "redirect:/login";
        model.addAttribute("email", email);
        return "verify-login-otp";
    }

    @PostMapping("/verify-login-otp")
    public String verifyLoginOtp(@RequestParam("otp") String otp, HttpSession session, Model model) {
        String email = (String) session.getAttribute("otpEmail");
        org.springframework.security.core.Authentication auth = (org.springframework.security.core.Authentication) session.getAttribute("pendingAuth");

        if (email != null && auth != null && otpService.verifyOtp(email, otp)) {
            // Manual Authentication
            org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(auth);
            
            session.removeAttribute("pendingAuth");
            session.removeAttribute("otpEmail");
            session.removeAttribute("otpPurpose");

            String role = auth.getAuthorities().iterator().next().getAuthority();
            if (role.equals("ROLE_JOB_SEEKER")) return "redirect:/student/dashboard";
            if (role.equals("ROLE_COMPANY")) return "redirect:/employer/dashboard";
            if (role.equals("ROLE_ADMIN") || role.equals("ROLE_SUPER_ADMIN")) return "redirect:/admin/dashboard";
            
            return "redirect:/";
        } else {
            model.addAttribute("error", "Invalid or expired OTP");
            model.addAttribute("email", email);
            return "verify-login-otp";
        }
    }

    @PostMapping("/resend-otp")
    public String resendOtp(HttpSession session, Model model) {
        String email = (String) session.getAttribute("otpEmail");
        String purpose = (String) session.getAttribute("otpPurpose");
        if (email != null) {
            otpService.generateAndSendOtp(email, purpose != null ? purpose : "REGISTER");
            model.addAttribute("success", "OTP resent successfully!");
            model.addAttribute("email", email);
            return "verify-otp";
        }
        return "redirect:/register";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, HttpSession session, Model model) {
        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute("error", "Email not found!");
            return "forgot-password";
        }
        
        otpService.generateAndSendOtp(email, "FORGOT_PASSWORD");
        session.setAttribute("otpEmail", email);
        session.setAttribute("otpPurpose", "FORGOT_PASSWORD");
        session.setAttribute("forgotPassword", "true");
        return "redirect:/verify-otp";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(HttpSession session, Model model) {
        String email = (String) session.getAttribute("resetEmail");
        if (email == null) {
            return "redirect:/login";
        }
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("password") String password, HttpSession session) {
        String email = (String) session.getAttribute("resetEmail");
        if (email != null) {
            userService.updatePassword(email, password);
            session.removeAttribute("resetEmail");
            session.removeAttribute("forgotPassword");
            session.removeAttribute("otpEmail");
            
            // Notification for reset
            emailService.sendEmail(email, "Password Reset Successful", "Your password has been successfully reset. If you didn't do this, please contact support.");
            
            return "redirect:/login?reset=true";
        }
        return "redirect:/login";
    }
}
