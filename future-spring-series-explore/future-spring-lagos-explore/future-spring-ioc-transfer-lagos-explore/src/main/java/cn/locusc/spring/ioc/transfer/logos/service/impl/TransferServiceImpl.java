package cn.locusc.spring.ioc.transfer.logos.service.impl;

import cn.locusc.spring.ioc.transfer.logos.dao.AccountDao;
import cn.locusc.spring.ioc.transfer.logos.dao.impl.JdbcAccountDaoImpl;
import cn.locusc.spring.ioc.transfer.logos.pojo.Account;
import cn.locusc.spring.ioc.transfer.logos.service.TransferService;

/**
 * @author 应癫
 */
public class TransferServiceImpl implements TransferService {

    private AccountDao accountDao = new JdbcAccountDaoImpl();

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
