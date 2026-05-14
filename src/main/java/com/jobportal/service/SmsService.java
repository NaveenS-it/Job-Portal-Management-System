package com.jobportal.service;

import org.springframework.stereotype.Service;

/**
 * Service for handling Mobile OTP Verification.
 * Currently architecture-ready for Twilio, Fast2SMS, or MSG91.
 */
@Service
public class SmsService {

    // Twilio / Fast2SMS API Key Placeholders
    private final String API_KEY = "YOUR_API_KEY_HERE";
    private final String SENDER_ID = "JOBPRT";

    /**
     * Placeholder for sending Mobile OTP.
     * @param phoneNumber recipient phone number
     * @param otp the generated OTP
     */
    public void sendSmsOtp(String phoneNumber, String otp) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return;
        }

        System.out.println("--- MOBILE SMS SERVICE ---");
        System.out.println("Target: " + phoneNumber);
        System.out.println("Message: Your Job Portal verification code is: " + otp);
        System.out.println("Status: Waiting for API integration...");
        System.out.println("--------------------------");

        // Integration Example (Twilio):
        /*
        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(phoneNumber),
                new com.twilio.type.PhoneNumber("YOUR_TWILIO_PHONE"),
                "Your OTP is: " + otp)
            .create();
        */
    }
}
