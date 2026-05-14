package com.jobportal.service;

import com.jobportal.entity.Application;
import com.jobportal.entity.ApplicationStatus;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private EmailService emailService;

    public void applyForJob(User user, Job job) {
        if (!hasApplied(user, job)) {
            Application application = new Application();
            application.setUser(user);
            application.setJob(job);
            application.setStatus(ApplicationStatus.APPLIED);
            applicationRepository.save(application);

            // Send email to applicant
            String companyName = job.getPostedBy().getCompanyName() != null ? job.getPostedBy().getCompanyName() : job.getPostedBy().getName();
            String applicantSubject = "Application Submitted - " + job.getTitle();
            String applicantContent = "Dear " + user.getName() + ",<br><br>" +
                    "Your application for the position of <b>" + job.getTitle() + "</b> at <b>" + companyName + "</b> has been successfully submitted.<br><br>" +
                    "Best Regards,<br>Job Portal Team";
            emailService.sendEmail(user.getEmail(), applicantSubject, applicantContent);

            // Send email to employer
            User employer = job.getPostedBy();
            if (employer != null) {
                String employerSubject = "New Application Received - " + job.getTitle();
                String employerContent = "Hello " + employer.getName() + ",<br><br>" +
                        "You have received a new application for the job <b>" + job.getTitle() + "</b> from <b>" + user.getName() + "</b>.<br>" +
                        "Please check your dashboard for details.<br><br>" +
                        "Best Regards,<br>Job Portal Team";
                emailService.sendEmail(employer.getEmail(), employerSubject, employerContent);
            }
        }
    }

    public boolean hasApplied(User user, Job job) {
        return applicationRepository.findByUserAndJob(user, job).isPresent();
    }

    public List<Application> getApplicationsByUser(User user) {
        return applicationRepository.findByUser(user);
    }

    public List<Application> getApplicationsByJob(Job job) {
        return applicationRepository.findByJob(job);
    }

    public Application findById(Long id) {
        return applicationRepository.findById(id).orElse(null);
    }

    @Transactional
    public void updateApplicationStatus(Long applicationId, ApplicationStatus status) {
        Application application = findById(applicationId);
        if (application != null) {
            application.setStatus(status);
            applicationRepository.save(application);
            
            // Send email for status update
            String companyName = application.getJob().getPostedBy().getCompanyName() != null ? application.getJob().getPostedBy().getCompanyName() : application.getJob().getPostedBy().getName();
            String applicantSubject = "Application Status Updated - " + application.getJob().getTitle();
            String applicantContent = "Dear " + application.getUser().getName() + ",<br><br>" +
                    "The status of your application for the position of <b>" + application.getJob().getTitle() + "</b> at <b>" + companyName + "</b> has been updated to: <b>" + status.name() + "</b>.<br><br>" +
                    "Best Regards,<br>Job Portal Team";
            emailService.sendEmail(application.getUser().getEmail(), applicantSubject, applicantContent);
        }
    }
    
    public long countApplications() {
        return applicationRepository.count();
    }
    
    public long countShortlisted() {
        return applicationRepository.countByStatus(ApplicationStatus.SHORTLISTED);
    }
}
