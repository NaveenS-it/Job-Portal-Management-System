package com.jobportal.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // true indicates HTML format
            
            mailSender.send(message);
        } catch (Throwable e) {
            System.err.println("Note: Email failed to send to " + to + " (SMTP not configured). Error: " + e.getMessage());
        }
    }

    public void sendOtpEmail(String to, String otp) {
        String subject = "Your OTP Verification Code";
        String content = "<div style='font-family: \"Inter\", sans-serif; max-width: 500px; margin: auto; padding: 30px; border: 1px solid #eee; border-radius: 12px; background: #fff; box-shadow: 0 4px 12px rgba(0,0,0,0.05);'>"
                + "<div style='text-align: center; margin-bottom: 25px;'>"
                + "<h1 style='color: #8B5CF6; font-size: 24px; font-weight: 800; margin: 0;'>JobBoard.</h1>"
                + "</div>"
                + "<p style='font-size: 16px; color: #374151; margin-bottom: 20px;'>Hello User,</p>"
                + "<p style='font-size: 15px; color: #4B5563; line-height: 1.6;'>Your OTP verification code is:</p>"
                + "<div style='background: #F3F4F6; padding: 24px; border-radius: 8px; text-align: center; margin: 20px 0;'>"
                + "<span style='font-size: 32px; font-weight: 800; letter-spacing: 8px; color: #111827;'>" + otp + "</span>"
                + "</div>"
                + "<p style='font-size: 14px; color: #6B7280; text-align: center; margin-bottom: 30px;'>Valid for 5 minutes. Do not share this code with anyone.</p>"
                + "<hr style='border: 0; border-top: 1px solid #E5E7EB; margin-bottom: 20px;'>"
                + "<p style='font-size: 13px; color: #9CA3AF; text-align: center; margin: 0;'>Job Portal Management System Team</p>"
                + "</div>";
        sendEmail(to, subject, content);
    }
}
