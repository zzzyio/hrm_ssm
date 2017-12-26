package org.deepsl.hrm.service.impl;

import org.deepsl.hrm.dao.JobDao;
import org.deepsl.hrm.domain.Job;
import org.deepsl.hrm.service.JobService;
import org.deepsl.hrm.util.tag.PageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

/**
 * Author guopop
 * Description
 * Date created in 2017/12/25 16:35
 */
@Service
public class JobServieImpl implements JobService{

    @Autowired
    JobDao jobDao;


    @Override
    public List<Job> findAllJob() {
        return jobDao.selectAllJob();
    }

    @Override
    public List<Job> findJob(Job job, PageModel pageModel) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("job", job);
        Integer count = jobDao.count(hashMap);
        pageModel.setRecordCount(count);
        hashMap.put("pageModel", pageModel);
        return jobDao.selectByPage(hashMap);
    }

    @Override
    public void removeJobById(Integer id) {
        jobDao.deleteById(id);
    }

    @Transactional
    @Override
    public void addJob(Job job) {
        jobDao.save(job);
    }

    @Override
    public Job findJobById(Integer id) {
        return jobDao.selectById(id);
    }

    @Transactional
    @Override
    public void modifyJob(Job job) {
        jobDao.update(job);
    }
}
