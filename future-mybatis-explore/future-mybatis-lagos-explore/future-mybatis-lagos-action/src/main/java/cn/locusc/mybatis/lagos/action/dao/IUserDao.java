package cn.locusc.mybatis.lagos.action.dao;

import cn.locusc.mybatis.lagos.action.pojo.User;

import java.io.IOException;
import java.util.List;

public interface IUserDao {

    List<User> findAll() throws IOException;

    // 多条件组合查询 演示if
    List<User> findByCondition(User user);

    // 多值查询 演示foreach
    List<User> findByIds(long[] ids);

}
