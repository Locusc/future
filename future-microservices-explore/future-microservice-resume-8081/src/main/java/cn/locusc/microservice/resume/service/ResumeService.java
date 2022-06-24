package cn.locusc.microservice.resume.service;

import cn.locusc.microservice.common.domain.po.Resume;

public interface ResumeService {

    Resume findDefaultResumeByUserId(Long userId);

}
