package com.jobportal.controller;

import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.security.CustomUserDetails;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.JobService;
import com.jobportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationService applicationService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userService.findById(userDetails.getId());
        model.addAttribute("user", user);
        model.addAttribute("applications", applicationService.getApplicationsByUser(user));
        return "student/dashboard";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @ModelAttribute User updatedUser,
                                @RequestParam("resume") MultipartFile resumeFile,
                                RedirectAttributes redirectAttributes) {
        updatedUser.setId(userDetails.getId());
        userService.updateUserProfile(updatedUser, resumeFile);
        redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        return "redirect:/student/dashboard";
    }

    @GetMapping("/jobs")
    public String showJobs(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        if (keyword != null) {
            model.addAttribute("jobs", jobService.searchJobs(keyword));
            model.addAttribute("keyword", keyword);
        } else {
            model.addAttribute("jobs", jobService.findAllJobs());
        }
        return "student/jobs";
    }

    @GetMapping("/apply/{jobId}")
    public String applyForJob(@PathVariable Long jobId, 
                              @AuthenticationPrincipal CustomUserDetails userDetails, 
                              RedirectAttributes redirectAttributes) {
        User user = userService.findById(userDetails.getId());
        Job job = jobService.findJobById(jobId);

        if (user.getResumePath() == null || user.getResumePath().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please upload your resume in the profile section before applying.");
            return "redirect:/student/jobs";
        }

        if (applicationService.hasApplied(user, job)) {
            redirectAttributes.addFlashAttribute("error", "You have already applied for this job.");
        } else {
            applicationService.applyForJob(user, job);
            redirectAttributes.addFlashAttribute("success", "Applied successfully for " + job.getTitle());
        }

        return "redirect:/student/jobs";
    }
}
