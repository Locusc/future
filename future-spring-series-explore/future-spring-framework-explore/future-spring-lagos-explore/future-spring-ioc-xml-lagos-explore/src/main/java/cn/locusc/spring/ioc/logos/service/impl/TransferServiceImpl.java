package cn.locusc.spring.ioc.logos.service.impl;

import cn.locusc.spring.ioc.logos.dao.AccountDao;
import cn.locusc.spring.ioc.logos.pojo.Account;
import cn.locusc.spring.ioc.logos.service.TransferService;

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
            // TransactionManager.getInstance().beginTransaction();
            System.out.println("执行转装业务逻辑");
            Account from = accountDao.queryAccountByCardNo(fromCardNo);
            Account to = accountDao.queryAccountByCardNo(toCardNo);

            from.setMoney(from.getMoney()-money);
            to.setMoney(to.getMoney()+money);

            accountDao.updateAccountByCardNo(to);
            // int c = 1/0;
            accountDao.updateAccountByCardNo(from);

            // 提交事务
            // ConnectionUtils.getInstance()
            //         .getCurrentThreadConnection().commit();
            // TransactionManager.getInstance().commit();
        } catch (Exception e) {
            // 回滚事务
            // ConnectionUtils.getInstance()
            //         .getCurrentThreadConnection().rollback();
            // TransactionManager.getInstance().rollback();
            throw e;
        }
    }
}
