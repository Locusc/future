package cn.locusc.spring.ioc.transfer.logos.service.impl;

import cn.locusc.spring.ioc.transfer.logos.dao.AccountDao;
import cn.locusc.spring.ioc.transfer.logos.dao.impl.JdbcAccountDaoImpl;
import cn.locusc.spring.ioc.transfer.logos.factory.BeanFactory;
import cn.locusc.spring.ioc.transfer.logos.pojo.Account;
import cn.locusc.spring.ioc.transfer.logos.service.TransferService;
import cn.locusc.spring.ioc.transfer.logos.utils.ConnectionUtils;
import cn.locusc.spring.ioc.transfer.logos.utils.TransactionManager;

/**
 * @author 应癫
 */
public class TransferServiceImpl implements TransferService {

    // private AccountDao accountDao = new JdbcAccountDaoImpl();

    // private AccountDao accountDao = (AccountDao) BeanFactory.getBean("accountDao");

    // 构造函数传参/set方法传参
    private AccountDao accountDao;

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public void transfer(String fromCardNo, String toCardNo, int money) throws Exception {
        try {
            // 开启事务(关闭事务的自动提交)
            // ConnectionUtils.getInstance()
            //         .getCurrentThreadConnection().setAutoCommit(false);
            TransactionManager.getInstance().beginTransaction();
            Account from = accountDao.queryAccountByCardNo(fromCardNo);
            Account to = accountDao.queryAccountByCardNo(toCardNo);

            from.setMoney(from.getMoney()-money);
            to.setMoney(to.getMoney()+money);

            accountDao.updateAccountByCardNo(to);
            int c = 1/0;
            accountDao.updateAccountByCardNo(from);

            // 提交事务
            // ConnectionUtils.getInstance()
            //         .getCurrentThreadConnection().commit();
            TransactionManager.getInstance().commit();
        } catch (Exception e) {
            // 回滚事务
            // ConnectionUtils.getInstance()
            //         .getCurrentThreadConnection().rollback();
            TransactionManager.getInstance().rollback();
            throw e;
        }
    }
}
