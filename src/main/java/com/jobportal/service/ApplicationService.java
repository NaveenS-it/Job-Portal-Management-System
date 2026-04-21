package com.jobportal.service;

import com.jobportal.entity.Application;
import com.jobportal.entity.ApplicationStatus;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {

    @Autowired
    private ApplicationRepository applicationRepository;

    public void applyForJob(User user, Job job) {
        if (!hasApplied(user, job)) {
            Application application = Application.builder()
                    .user(user)
                    .job(job)
                    .status(ApplicationStatus.APPLIED)
                    .build();
            applicationRepository.save(application);
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

    public void updateApplicationStatus(Long applicationId, ApplicationStatus status) {
        Application application = findById(applicationId);
        if (application != null) {
            application.setStatus(status);
            applicationRepository.save(application);
            
            // Optionally, we could send an email here for SHORTLISTED status
        }
    }
    
    public long countApplications() {
        return applicationRepository.count();
    }
    
    public long countShortlisted() {
        return applicationRepository.countByStatus(ApplicationStatus.SHORTLISTED);
    }
}
