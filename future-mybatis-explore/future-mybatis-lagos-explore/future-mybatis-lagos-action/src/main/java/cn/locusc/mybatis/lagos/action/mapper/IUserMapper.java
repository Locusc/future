package cn.locusc.mybatis.lagos.action.mapper;

import cn.locusc.mybatis.lagos.action.pojo.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.mybatis.caches.redis.RedisCache;

import java.util.List;

// 开启二级缓存
// implementation 实现缓存的类
@CacheNamespace(implementation = RedisCache.class)
public interface IUserMapper {

    // 查询所有用户信息, 同时查询出每个用户关联的订单信息
    List<User> findAll();

    // 查询所有用户 同时查询每个用户关联的角色信息
    List<User> findAllUserAndRole();

    // 添加用户
    @Insert("insert into sys_user(id,account_name) values(#{id},#{account_name})")
    void addUser(User user);

    // 更新用户
    @Update("update sys_user set account_name = #{account_name} where id = #{id}")
    void updateUser(User user);

    // 查询用户
    @Select("select * from sys_user")
    List<User> selectUser();

    // 删除用户
    @Delete("delete from sys_user where id = #{id}")
    void deleteUser(Long id);

    // 根据id查询用户
    @Options(useCache = true) // 注解设置二级缓存
    @Select("select * from sys_user where id = #{id}")
    User findUserById(Long id);

    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "account_name", column = "account_name"),
            @Result(property = "orderList", column = "id",
                    javaType = List.class,
                    many = @Many(select = "cn.locusc.mybatis.lagos.action.mapper.IOrderMapper.findOrderByUserId"))
    })
    @Select("select * from sys_user")
    List<User> findAllAnnotation();


    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "account_name", column = "account_name"),
            @Result(property = "roleList", column = "id",
                    javaType = List.class,
                    many = @Many(select = "cn.locusc.mybatis.lagos.action.mapper.IRoleMapper.findRoleByUid"))
    })
    @Select("select * from sys_user")
    List<User> findAllUserAndRoleAnnotation();

}
