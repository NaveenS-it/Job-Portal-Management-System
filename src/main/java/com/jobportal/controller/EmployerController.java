package com.jobportal.controller;

import com.jobportal.entity.Application;
import com.jobportal.entity.ApplicationStatus;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.entity.CompanyStatus;
import com.jobportal.security.CustomUserDetails;
import com.jobportal.service.ApplicationService;
import com.jobportal.service.JobService;
import com.jobportal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/employer")
public class EmployerController {

    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationService applicationService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User employer = userService.findById(userDetails.getId());
        List<Job> myJobs = jobService.findJobsByEmployer(employer);
        model.addAttribute("jobsCount", myJobs.size());
        
        long totalApps = 0;
        for (Job job : myJobs) {
            totalApps += applicationService.getApplicationsByJob(job).size();
        }
        model.addAttribute("applicationsCount", totalApps);
        model.addAttribute("jobs", myJobs);
        model.addAttribute("employer", employer);

        if (employer.getCompanyStatus() != CompanyStatus.APPROVED) {
            model.addAttribute("warning", "Your account is pending verification. Some features may be restricted.");
        }

        return "employer/dashboard";
    }

    @GetMapping("/job/post")
    public String showPostJobForm(Model model) {
        model.addAttribute("job", new Job());
        return "employer/post-job";
    }

    @PostMapping("/job/post")
    public String postJob(@Valid @ModelAttribute("job") Job job, 
                          BindingResult result, 
                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (result.hasErrors()) {
            return "employer/post-job";
        }
        User employer = userService.findById(userDetails.getId());
        
        if (employer.getCompanyStatus() != CompanyStatus.APPROVED) {
            return "redirect:/employer/dashboard?error=You must be approved by admin to post jobs.";
        }

        jobService.saveJob(job, employer);
        return "redirect:/employer/dashboard?success=Job posted successfully";
    }

    @GetMapping("/job/{jobId}/applicants")
    public String viewApplicants(@PathVariable Long jobId, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Job job = jobService.findJobById(jobId);
        
        // Ensure the job belongs to this employer
        if(!job.getPostedBy().getId().equals(userDetails.getId())) {
            return "redirect:/employer/dashboard";
        }

        model.addAttribute("job", job);
        model.addAttribute("applications", applicationService.getApplicationsByJob(job));
        return "employer/applicants";
    }

    @PostMapping("/application/{applicationId}/status")
    public String updateApplicationStatus(@PathVariable Long applicationId, 
                                          @RequestParam("status") ApplicationStatus status,
                                          RedirectAttributes redirectAttributes) {
        Application app = applicationService.findById(applicationId);
        applicationService.updateApplicationStatus(applicationId, status);
        redirectAttributes.addFlashAttribute("success", "Status updated successfully.");
        return "redirect:/employer/job/" + app.getJob().getId() + "/applicants";
    }
    
    @PostMapping("/job/{jobId}/delete")
    public String deleteJob(@PathVariable Long jobId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Job job = jobService.findJobById(jobId);
        if(job.getPostedBy().getId().equals(userDetails.getId())) {
            jobService.deleteJob(jobId);
        }
        return "redirect:/employer/dashboard";
    }
}
