package com.jobportal.config;

import com.jobportal.entity.Role;
import com.jobportal.entity.User;
import com.jobportal.entity.Job;
import com.jobportal.entity.CompanyStatus;
import com.jobportal.repository.UserRepository;
import com.jobportal.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;
    
    @Autowired
    private com.jobportal.repository.ApplicationRepository applicationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        // 1. Cleanup Old Data if it exists (Transition to new workflow)
        if (userRepository.findByEmail("admin@jobportal.com").isPresent() || 
            userRepository.findByEmail("hr@technova.com").isPresent()) {
            System.out.println("Cleaning up old seeded data for fresh workflow...");
            applicationRepository.deleteAll();
            jobRepository.deleteAll();
            userRepository.deleteAll();
            System.out.println("Database cleared.");
        }

        // 2. Add New Admin if not exists - Using your email
        if (userRepository.findByEmail("sn770324@gmail.com").isEmpty()) {
            User admin = User.builder()
                    .name("System Admin")
                    .email("sn770324@gmail.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ROLE_ADMIN)
                    .isEnabled(true)
                    .build();
            userRepository.save(admin);
            System.out.println("Seeded Admin: sn770324@gmail.com (Pass: admin123)");
        }
    }
}
