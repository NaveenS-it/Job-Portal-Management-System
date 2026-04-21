package com.jobportal.controller;

import com.jobportal.entity.Role;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.JobService;
import com.jobportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private JobService jobService;

    @Autowired
    private ApplicationService applicationService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalStudents", userService.countUsersByRole(Role.ROLE_STUDENT));
        model.addAttribute("totalEmployers", userService.countUsersByRole(Role.ROLE_EMPLOYER));
        model.addAttribute("totalJobs", jobService.countJobs());
        model.addAttribute("totalApplications", applicationService.countApplications());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "admin/users";
    }

    @GetMapping("/jobs")
    public String manageJobs(Model model) {
        model.addAttribute("jobs", jobService.findAllJobs());
        return "admin/jobs";
    }

    @PostMapping("/job/{jobId}/delete")
    public String deleteJob(@PathVariable Long jobId) {
        jobService.deleteJob(jobId);
        return "redirect:/admin/jobs";
    }
}
