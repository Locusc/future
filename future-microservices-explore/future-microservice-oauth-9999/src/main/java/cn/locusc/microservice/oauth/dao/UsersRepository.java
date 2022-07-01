package cn.locusc.microservice.oauth.dao;


import cn.locusc.microservice.common.domain.po.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users,Long> {

    Users findByUsername(String username);

}
