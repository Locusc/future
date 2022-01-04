package cn.locusc.mybatis.lagos.action.mapper;

import cn.locusc.mybatis.lagos.action.pojo.Role;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface IRoleMapper {

    @Select("select * from test_role r, test_user_role ur where r.id = ur.role_id and ur.user_id = #{uid}")
    List<Role> findRoleByUid(Long uid);

}
