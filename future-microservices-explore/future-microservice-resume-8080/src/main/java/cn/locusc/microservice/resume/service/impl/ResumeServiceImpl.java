package cn.locusc.microservice.resume.service.impl;

import cn.locusc.microservice.resume.dao.ResumeDao;
import cn.locusc.microservice.resume.service.ResumeService;
import cn.locusc.microservice.common.domain.po.Resume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

@Service
public class ResumeServiceImpl implements ResumeService {

    @Autowired
    private ResumeDao resumeDao;

    @Override
    public Resume findDefaultResumeByUserId(Long userId) {
        Resume resume = new Resume();
        resume.setUserId(userId);
        // 查询默认简历
        resume.setIsDefault(1);
        Example<Resume> example = Example.of(resume);
        return resumeDao.findOne(example).get();
    }

}
