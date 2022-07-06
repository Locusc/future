package cn.locusc.microservice.resume.nsd.dao;

import cn.locusc.microservice.common.domain.po.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeDao extends JpaRepository<Resume,Long> {
}

