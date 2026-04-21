package com.jobportal.service;

import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import com.jobportal.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    public void saveJob(Job job, User employer) {
        job.setPostedBy(employer);
        jobRepository.save(job);
    }

    public List<Job> findAllJobs() {
        return jobRepository.findAll();
    }

    public List<Job> findJobsByEmployer(User employer) {
        return jobRepository.findByPostedBy(employer);
    }

    public Job findJobById(Long id) {
        return jobRepository.findById(id).orElse(null);
    }

    public List<Job> searchJobs(String keyword, String category) {
        return jobRepository.searchAdvanced(keyword, category);
    }
    
    // For backward compatibility if needed
    public List<Job> searchJobs(String keyword) {
        return jobRepository.searchAdvanced(keyword, null);
    }
    
    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }
    
    public long countJobs() {
        return jobRepository.count();
    }
}
