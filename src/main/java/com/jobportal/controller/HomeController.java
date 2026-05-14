package com.jobportal.controller;

import com.jobportal.entity.Role;
import com.jobportal.entity.CompanyStatus;
import com.jobportal.service.JobService;
import com.jobportal.service.UserService;
import com.jobportal.service.ApplicationService;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;

@Controller
public class HomeController {

    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationService applicationService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("jobs", jobService.findAllJobs().stream().limit(6).collect(Collectors.toList()));
        
        // Approved Companies
        model.addAttribute("companies", userService.findUsersByRole(Role.ROLE_COMPANY)
                .stream()
                .filter(c -> c.getCompanyStatus() == CompanyStatus.APPROVED)
                .limit(4)
                .collect(Collectors.toList()));

        // Stats
        model.addAttribute("totalJobs", jobService.countJobs());
        model.addAttribute("totalCompanies", userService.countUsersByRole(Role.ROLE_COMPANY));
        model.addAttribute("totalApplicants", userService.countUsersByRole(Role.ROLE_JOB_SEEKER));
        model.addAttribute("totalApplications", applicationService.countApplications());

        return "index";
    }

    @GetMapping("/jobs")
    public String searchJobs(@RequestParam(value = "keyword", required = false) String keyword, 
                             @RequestParam(value = "category", required = false) String category, 
                             @RequestParam(value = "jobType", required = false) String jobType,
                             @RequestParam(value = "workMode", required = false) String workMode,
                             @RequestParam(value = "experience", required = false) String experience,
                             Model model) {
        model.addAttribute("jobs", jobService.searchJobs(keyword, category, jobType, workMode, experience));
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("jobType", jobType);
        model.addAttribute("workMode", workMode);
        model.addAttribute("experience", experience);
        return "index";
    }

    @GetMapping("/jobs/{id}")
    public String viewJob(@PathVariable("id") Long id, Model model) {
        model.addAttribute("job", jobService.findJobById(id));
        return "job-details";
    }

    @GetMapping("/favicon.ico")
    public ResponseEntity<Void> favicon() {
        return ResponseEntity.noContent().build();
    }
}
