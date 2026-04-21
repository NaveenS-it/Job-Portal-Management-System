package com.jobportal.service;

import com.jobportal.entity.Role;
import com.jobportal.entity.User;
import com.jobportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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

    public void registerUser(User user, String role) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.valueOf(role));
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
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
        }
    }
    
    public long countUsersByRole(Role role) {
        return userRepository.countByRole(role);
    }
}
