package cn.locusc.spring.security.lagos.service;

import cn.locusc.spring.security.lagos.domain.Permission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface PermissionService extends IService<Permission> {
    // 根据用户ID查询对应的权限
    List<Permission> findByUserId(Integer id);
}
