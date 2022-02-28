package cn.locusc.spring.ssm.lagos.service;

import cn.locusc.spring.ssm.lagos.pojo.Account;

import java.util.List;

public interface AccountService {
    List<Account> queryAccountList() throws Exception;
}
