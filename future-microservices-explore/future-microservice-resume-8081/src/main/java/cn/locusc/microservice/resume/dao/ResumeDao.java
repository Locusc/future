package cn.locusc.microservice.resume.dao;

import cn.locusc.microservice.common.domain.po.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeDao extends JpaRepository<Resume,Long> {
}
