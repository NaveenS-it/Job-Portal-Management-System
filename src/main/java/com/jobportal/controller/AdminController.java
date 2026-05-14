package com.jobportal.controller;

import com.jobportal.entity.Role;
import com.jobportal.entity.User;
import com.jobportal.entity.CompanyStatus;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.EmailService;
import com.jobportal.service.JobService;
import com.jobportal.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
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

    @Autowired
    private EmailService emailService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalStudents", userService.countUsersByRole(Role.ROLE_JOB_SEEKER));
        model.addAttribute("totalEmployers", userService.countUsersByRole(Role.ROLE_COMPANY));
        model.addAttribute("totalJobs", jobService.countJobs());
        model.addAttribute("totalApplications", applicationService.countApplications());
        
        // Find pending companies
        long pendingCompanies = userService.findUsersByRole(Role.ROLE_COMPANY)
                                .stream()
                                .filter(u -> u.getCompanyStatus() == CompanyStatus.PENDING)
                                .count();
        model.addAttribute("pendingApprovals", pendingCompanies);
        
        return "admin/dashboard";
    }

    @GetMapping("/approvals")
    public String viewApprovals(Model model) {
        List<User> pendingCompanies = userService.findUsersByRole(Role.ROLE_COMPANY)
                                .stream()
                                .filter(u -> u.getCompanyStatus() == CompanyStatus.PENDING)
                                .collect(Collectors.toList());
        model.addAttribute("companies", pendingCompanies);
        return "admin/approvals";
    }

    @PostMapping("/approve/{id}")
    public String approveCompany(@PathVariable Long id) {
        User company = userService.findById(id);
        if (company != null) {
            company.setCompanyStatus(CompanyStatus.APPROVED);
            userService.saveUser(company);
            
            // Send Approval Email
            emailService.sendEmail(company.getEmail(), "Company Approved - JobBoard", 
                "Congratulations! Your company account has been approved. You can now login and start posting jobs.");
        }
        return "redirect:/admin/approvals?success=approved";
    }

    @PostMapping("/reject/{id}")
    public String rejectCompany(@PathVariable Long id) {
        User company = userService.findById(id);
        if (company != null) {
            company.setCompanyStatus(CompanyStatus.REJECTED);
            userService.saveUser(company);
            
            // Send Rejection Email
            emailService.sendEmail(company.getEmail(), "Company Account Status - JobBoard", 
                "We regret to inform you that your company account request has been rejected. Please contact support for more details.");
        }
        return "redirect:/admin/approvals?success=rejected";
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
