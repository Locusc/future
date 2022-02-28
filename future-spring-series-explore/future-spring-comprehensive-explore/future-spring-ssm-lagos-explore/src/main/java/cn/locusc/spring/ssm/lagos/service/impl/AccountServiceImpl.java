package cn.locusc.spring.ssm.lagos.service.impl;

import cn.locusc.spring.ssm.lagos.mapper.AccountMapper;
import cn.locusc.spring.ssm.lagos.pojo.Account;
import cn.locusc.spring.ssm.lagos.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public List<Account> queryAccountList() throws Exception {
        return accountMapper.queryAccountList();
    }
}
