package cn.locusc.microservice.resume.nsd.service;

import cn.locusc.microservice.common.domain.po.Resume;
import cn.locusc.microservice.common.service.ResumeService;
import cn.locusc.microservice.resume.nsd.dao.ResumeDao;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

@Service
public class ResumeServiceImpl implements ResumeService {

    @Autowired
    private ResumeDao resumeDao;

    @Override
    public Integer findDefaultResumeByUserId(Long userId) {
        Resume resume = new Resume();
        resume.setUserId(userId);
        // 查询默认简历
        resume.setIsDefault(1);
        Example<Resume> example = Example.of(resume);
        //return resumeDao.findOne(example).get().getIsOpenResume();
        return 8084;
    }

}
