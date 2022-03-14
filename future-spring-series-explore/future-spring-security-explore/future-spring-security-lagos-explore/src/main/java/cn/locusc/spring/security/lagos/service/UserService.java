package cn.locusc.spring.security.lagos.service;

import cn.locusc.spring.security.lagos.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface UserService extends IService<User> {

    // 根据用户名查询用户
    public User findByUsername(String username);

}
