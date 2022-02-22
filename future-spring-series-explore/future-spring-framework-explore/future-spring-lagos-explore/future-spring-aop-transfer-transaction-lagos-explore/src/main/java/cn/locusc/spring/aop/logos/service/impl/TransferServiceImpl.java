package cn.locusc.spring.aop.logos.service.impl;

import cn.locusc.spring.aop.logos.dao.AccountDao;
import cn.locusc.spring.aop.logos.pojo.Account;
import cn.locusc.spring.aop.logos.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author 应癫
 */
@Service("transferService")
@Transactional
public class TransferServiceImpl implements TransferService {

    @Autowired
    @Qualifier("accountDao")
    private AccountDao accountDao;

    @Override
    public void transfer(String fromCardNo, String toCardNo, int money) throws Exception {

        Account from = accountDao.queryAccountByCardNo(fromCardNo);
        Account to = accountDao.queryAccountByCardNo(toCardNo);

        from.setMoney(from.getMoney()-money);
        to.setMoney(to.getMoney()+money);

        accountDao.updateAccountByCardNo(to);
        int c = 1/0;
        accountDao.updateAccountByCardNo(from);

    }
}
