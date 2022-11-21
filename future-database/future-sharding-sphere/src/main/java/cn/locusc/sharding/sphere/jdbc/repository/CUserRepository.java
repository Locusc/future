package cn.locusc.sharding.sphere.jdbc.repository;

import cn.locusc.sharding.sphere.jdbc.entity.CUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CUserRepository extends JpaRepository<CUser, Long> {

    List<CUser> findByPwd(String pwd);

}
