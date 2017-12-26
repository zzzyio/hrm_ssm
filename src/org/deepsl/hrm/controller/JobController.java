package org.deepsl.hrm.controller;

import java.util.Arrays;
import java.util.List;

import org.deepsl.hrm.domain.Job;
import org.deepsl.hrm.service.HrmService;
import org.deepsl.hrm.service.JobService;
import org.deepsl.hrm.util.tag.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**   
 * @Description: 处理职位请求控制器  
 * @version V1.0   
 */

@RequestMapping("job")
@Controller
public class JobController {

    @Autowired
    JobService jobService;


    @RequestMapping("selectJob")
    public String selectJob(Job job, PageModel pageModel, Model model) {
        List<Job> jobs = jobService.findJob(job, pageModel);
        model.addAttribute("jobs", jobs);
        model.addAttribute("pageModel", pageModel);
        return "job/job";
    }

    @RequestMapping("addJob")
    public String addJob(String flag, Job job) {
        if ("1".equals(flag)) {
            return "job/showAddJob";
        }
        if ("2".equals(flag)) {
            jobService.addJob(job);
            return "redirect:selectJob";
        }
        return "404.html";
    }

    @RequestMapping("updateJob")
    public String updateJob(String flag, Job job, Model model) {
        if ("1".equals(flag)) {
            Job jobById = jobService.findJobById(job.getId());
            model.addAttribute("job", jobById);
            return "job/showUpdateJob";
        }
        if ("2".equals(flag)) {
            jobService.modifyJob(job);
            return "redirect:selectJob";
        }
        return "404.html";
    }

    @RequestMapping("removeJob")
    public String removeJob(String[] ids) {
        List<String> idsStr = Arrays.asList(ids);

        for (String id: idsStr) {
            int i = Integer.parseInt(id);
            jobService.removeJobById(i);
        }
        return "redirect:selectJob";
    }
 
}
