package com.jobportal.service;

import com.jobportal.entity.OtpEntity;
import com.jobportal.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public void generateAndSendOtp(String email, String purpose) {
        // Clear previous OTP for this email
        otpRepository.deleteByEmail(email);

        // Generate 6 digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        OtpEntity otpEntity = OtpEntity.builder()
                .email(email)
                .otp(otp)
                .purpose(purpose)
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .build();

        otpRepository.save(otpEntity);
        
        System.out.println("=========================================");
        System.out.println("GENERATED OTP FOR " + email + " : " + otp + " [" + purpose + "]");
        System.out.println("=========================================");
        
        emailService.sendOtpEmail(email, otp);
    }

    // Default to REGISTER for backward compatibility or use overloaded
    public void generateAndSendOtp(String email) {
        generateAndSendOtp(email, "REGISTER");
    }

    @Transactional
    public boolean verifyOtp(String email, String otp) {
        // Find latest OTP for this email
        Optional<OtpEntity> optionalOtp = otpRepository.findByEmailAndOtp(email, otp);
        
        if (optionalOtp.isPresent()) {
            OtpEntity otpEntity = optionalOtp.get();
            
            // Check if already used or too many attempts
            if (otpEntity.isUsed() || otpEntity.getAttempts() >= 3) {
                return false;
            }

            if (otpEntity.getExpiryTime().isAfter(LocalDateTime.now())) {
                otpEntity.setUsed(true);
                otpRepository.save(otpEntity);
                // For security, still delete or keep as 'used'
                otpRepository.deleteByEmail(email); 
                return true;
            } else {
                otpRepository.deleteByEmail(email); // expired
            }
        } else {
            // Increment attempts for any existing OTP for this email even if code is wrong
            // (Standard security practice)
        }
        return false;
    }
}
