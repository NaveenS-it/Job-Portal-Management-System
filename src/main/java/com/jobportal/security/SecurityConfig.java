package com.jobportal.security;

import com.jobportal.entity.User;
import com.jobportal.entity.Role;
import com.jobportal.entity.CompanyStatus;
import com.jobportal.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private OtpService otpService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/register", "/verify-otp", "/verify-login-otp", "/resend-otp", "/forgot-password", "/reset-password", "/api/chat", "/css/**", "/js/**", "/images/**", "/jobs", "/uploads/**", "/favicon.ico").permitAll()
                .requestMatchers("/student/**").hasAuthority("ROLE_JOB_SEEKER")
                .requestMatchers("/employer/**").hasAuthority("ROLE_COMPANY")
                .requestMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler((request, response, authentication) -> {
                    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                    User user = userDetails.getUser();
                    
                    // 1. Check Email Verification
                    if (!user.isEnabled()) {
                        request.getSession().setAttribute("pendingEmail", user.getEmail());
                        org.springframework.security.core.context.SecurityContextHolder.clearContext();
                        response.sendRedirect("/verify-otp?error=not-verified");
                        return;
                    }

                    // 2. Check Company Approval
                    if (user.getRole() == Role.ROLE_COMPANY && user.getCompanyStatus() != CompanyStatus.APPROVED) {
                        org.springframework.security.core.context.SecurityContextHolder.clearContext();
                        response.sendRedirect("/login?error=pending-approval");
                        return;
                    }

                    // 3. 2FA - Generate Login OTP
                    request.getSession().setAttribute("pendingAuth", authentication);
                    request.getSession().setAttribute("otpEmail", user.getEmail());
                    request.getSession().setAttribute("otpPurpose", "LOGIN");
                    
                    // In a real prod app, you might only do 2FA for sensitive roles or if enabled
                    // For this requirement, we do it for everyone
                    // We clear context so they can't access dashboards yet
                    org.springframework.security.core.context.SecurityContextHolder.clearContext();
                    
                    // We need to inject OtpService here, but handlers are usually beans or we use static context
                    // Since SecurityConfig is a bean, we can @Autowired OtpService
                    otpService.generateAndSendOtp(user.getEmail(), "LOGIN");
                    
                    response.sendRedirect("/verify-login-otp");
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}
