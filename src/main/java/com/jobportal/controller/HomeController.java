package com.jobportal.controller;

import com.jobportal.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private JobService jobService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("jobs", jobService.findAllJobs());
        return "index";
    }

    @GetMapping("/jobs")
    public String searchJobs(@RequestParam(value = "keyword", required = false) String keyword, 
                             @RequestParam(value = "category", required = false) String category, 
                             Model model) {
        model.addAttribute("jobs", jobService.searchJobs(keyword, category));
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        return "index"; // You can point this to a separate "all jobs" page if you had one. We'll reuse index for simplicity.
    }
}
