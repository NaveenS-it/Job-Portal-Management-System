package com.jobportal.service;

import com.jobportal.entity.Role;
import com.jobportal.entity.User;
import com.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String UPLOAD_DIR = "uploads/";

    @Transactional
    public void registerUser(User user, String role) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.valueOf(role));
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    
    public void saveUser(User user) {
        userRepository.save(user);
    }
    
    @Transactional
    public void activateUser(String email) {
        User user = findByEmail(email);
        if (user != null) {
            user.setEnabled(true);
            userRepository.save(user);
        }
    }
    
    @Transactional
    public void updatePassword(String email, String newPassword) {
        User user = findByEmail(email);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
    }
    
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
    
    public List<User> findUsersByRole(Role role) {
        return userRepository.findAll().stream().filter(u -> u.getRole() == role).toList();
    }

    @Transactional
    public void updateUserProfile(User updatedUser, MultipartFile resumeFile) {
        User existingUser = userRepository.findById(updatedUser.getId()).orElse(null);
        if (existingUser != null) {
            existingUser.setName(updatedUser.getName());
            existingUser.setPhone(updatedUser.getPhone());
            existingUser.setSkills(updatedUser.getSkills());
            existingUser.setExperience(updatedUser.getExperience());
            existingUser.setLocation(updatedUser.getLocation());
            existingUser.setAbout(updatedUser.getAbout());
            existingUser.setGithubProfile(updatedUser.getGithubProfile());
            existingUser.setLinkedinProfile(updatedUser.getLinkedinProfile());
            existingUser.setCompanyName(updatedUser.getCompanyName());

            if (resumeFile != null && !resumeFile.isEmpty()) {
                try {
                    Path uploadPath = Paths.get(UPLOAD_DIR);
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                    String fileName = existingUser.getId() + "_" + resumeFile.getOriginalFilename();
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(resumeFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    existingUser.setResumePath(fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            userRepository.save(existingUser);
            calculateAndSaveProfileScore(existingUser.getId());
        }
    }

    @Transactional
    public void calculateAndSaveProfileScore(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;

        int score = 0;
        if (user.getName() != null && !user.getName().isEmpty()) score += 10;
        if (user.getEmail() != null && !user.getEmail().isEmpty()) score += 10;
        if (user.getPhone() != null && !user.getPhone().isEmpty()) score += 10;
        if (user.getLocation() != null && !user.getLocation().isEmpty()) score += 10;
        if (user.getAbout() != null && !user.getAbout().isEmpty()) score += 10;
        if (user.getSkills() != null && !user.getSkills().isEmpty()) score += 15;
        if (user.getExperience() != null && !user.getExperience().isEmpty()) score += 10;
        if (user.getResumePath() != null && !user.getResumePath().isEmpty()) score += 25;

        user.setProfileScore(score);
        userRepository.save(user);
    }
    
    public long countUsersByRole(Role role) {
        return userRepository.countByRole(role);
    }
}
