package cn.locusc.mybatis.lagos.action.mapper;

import cn.locusc.mybatis.lagos.action.pojo.Order;
import cn.locusc.mybatis.lagos.action.pojo.User;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface IOrderMapper {

    // 查询订单的同时还查询该订单所属的用户
    List<Order> findOrderAndUser();

    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "orderTime", column = "orderTime"),
            @Result(property = "total", column = "total"),
            // 这里的column = "uid"就是传递给findUserById的参数
            @Result(property = "user", column = "uid",
                    javaType = User.class,
                    one = @One(select = "cn.locusc.mybatis.lagos.action.mapper.IUserMapper.findUserById"))
    })
    @Select("select * from test_order")
    List<Order> findOrderAndUserAnnotation();

    // 根据userId查询
    @Select("select * from test_order where uid = #{uid}")
    List<Order> findOrderByUserId(Long uid);

}
