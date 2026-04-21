package com.jobportal.config;

import com.jobportal.entity.Job;
import com.jobportal.entity.Role;
import com.jobportal.entity.User;
import com.jobportal.repository.JobRepository;
import com.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        
        // 1. Create Default Admin if it doesn't exist
        if (userRepository.findByEmail("admin@jobportal.com").isEmpty()) {
            User admin = User.builder()
                    .name("System Admin")
                    .email("admin@jobportal.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ROLE_ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("Default Admin Account Created.");
        }

        // 2. Create Default Companies (Employers) and Jobs for the Dashboard Mock Data
        if (jobRepository.count() == 0) {
            System.out.println("No jobs found, initializing mock companies and jobs...");

            // Company 1: TechNova
            User employer1 = userRepository.findByEmail("hr@technova.com").orElseGet(() -> {
                return userRepository.save(User.builder()
                        .name("HR Manager")
                        .companyName("TechNova Web Services")
                        .email("hr@technova.com")
                        .password(passwordEncoder.encode("demo123"))
                        .role(Role.ROLE_EMPLOYER)
                        .location("San Francisco, CA")
                        .build());
            });

            Job job1 = Job.builder()
                    .title("Senior Cloud Architect")
                    .description("We are looking for an experienced Cloud Architect to deploy and manage our enterprise AWS infrastructure. You will deal with Kubernetes, Docker, and high availability systems.")
                    .category("IT")
                    .skillsRequired("AWS, Kubernetes, Docker, Terraform")
                    .experience("5-8 Years")
                    .salary("$130,000 - $160,000")
                    .jobType("Full-Time")
                    .workMode("Remote")
                    .location("San Francisco, CA")
                    .postedBy(employer1)
                    .build();

            // Company 2: FinSecure Corp
            User employer2 = userRepository.findByEmail("careers@finsecure.com").orElseGet(() -> {
                return userRepository.save(User.builder()
                        .name("Hiring Team")
                        .companyName("FinSecure Banking")
                        .email("careers@finsecure.com")
                        .password(passwordEncoder.encode("demo123"))
                        .role(Role.ROLE_EMPLOYER)
                        .location("New York, NY")
                        .build());
            });

            Job job2 = Job.builder()
                    .title("Financial Data Analyst")
                    .description("Join our dynamic banking team to analyze high-frequency trading data. Strong mathematical and python background expected.")
                    .category("Finance")
                    .skillsRequired("Python, SQL, Tableau, Pandas")
                    .experience("2-4 Years")
                    .salary("$90,000 - $110,000")
                    .jobType("Full-Time")
                    .workMode("Hybrid")
                    .location("New York, NY")
                    .postedBy(employer2)
                    .build();

            // Company 3: PixelPerfect Designs
            User employer3 = userRepository.findByEmail("recruitment@pixelperfect.com").orElseGet(() -> {
                return userRepository.save(User.builder()
                        .name("Creative Director")
                        .companyName("PixelPerfect Agency")
                        .email("recruitment@pixelperfect.com")
                        .password(passwordEncoder.encode("demo123"))
                        .role(Role.ROLE_EMPLOYER)
                        .location("London, UK")
                        .build());
            });

            Job job3 = Job.builder()
                    .title("Lead UI/UX Designer")
                    .description("Design beautiful SaaS products. Must have an excellent eye for typography, modern grid systems, and glassmorphic designs.")
                    .category("Design")
                    .skillsRequired("Figma, Adobe XD, CSS, Wireframing")
                    .experience("3-5 Years")
                    .salary("$85,000 - $105,000")
                    .jobType("Contract")
                    .workMode("Remote")
                    .location("London, UK")
                    .postedBy(employer3)
                    .build();

            // Company 4: AeroSpace Dynamics
            User employer4 = userRepository.findByEmail("jobs@aerospace.com").orElseGet(() -> {
                return userRepository.save(User.builder()
                        .name("Engineering Lead")
                        .companyName("AeroSpace Dynamics")
                        .email("jobs@aerospace.com")
                        .password(passwordEncoder.encode("demo123"))
                        .role(Role.ROLE_EMPLOYER)
                        .location("Austin, TX")
                        .build());
            });

            Job job4 = Job.builder()
                    .title("Robotics Engineer")
                    .description("Help us build the next generation of autonomous drones. Familiarity with ROS, C++, and hardware integration required.")
                    .category("Engineering")
                    .skillsRequired("C++, ROS, Hardware, Embedded")
                    .experience("4-6 Years")
                    .salary("$110,000 - $140,000")
                    .jobType("Full-Time")
                    .workMode("On-Site")
                    .location("Austin, TX")
                    .postedBy(employer4)
                    .build();

            // Additional jobs attached to existing companies to make it populated
            Job job5 = Job.builder()
                    .title("Frontend Vue.js Developer")
                    .description("We use Vue3 and Tailwind to build fast and responsive enterprise dashboards.")
                    .category("IT")
                    .skillsRequired("Vue.js, JavaScript, Tailwind, HTML/CSS")
                    .experience("1-3 Years")
                    .salary("$75,000 - $95,000")
                    .jobType("Full-Time")
                    .workMode("Hybrid")
                    .location("San Francisco, CA")
                    .postedBy(employer1) // TechNova
                    .build();

            Job job6 = Job.builder()
                    .title("Digital Marketing Specialist")
                    .description("Manage our Google Ads and SEO strategies to boost product visibility.")
                    .category("Marketing")
                    .skillsRequired("SEO, Google Ads, Content Strategy")
                    .experience("2-4 Years")
                    .salary("$70,000 - $85,000")
                    .jobType("Part-Time")
                    .workMode("Remote")
                    .location("London, UK")
                    .postedBy(employer3) // PixelPerfect
                    .build();

            jobRepository.saveAll(Arrays.asList(job1, job2, job3, job4, job5, job6));
            System.out.println("Mock Companies and Jobs successfully injected!");
        }
    }
}
