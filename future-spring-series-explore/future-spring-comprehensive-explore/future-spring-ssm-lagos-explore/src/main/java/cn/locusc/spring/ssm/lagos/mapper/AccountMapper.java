package cn.locusc.spring.ssm.lagos.mapper;

import cn.locusc.spring.ssm.lagos.pojo.Account;

import java.util.List;

public interface AccountMapper {

    //  定义dao层接口方法 -> 查询account表所有数据
    List<Account> queryAccountList() throws Exception;

}
