package cn.locusc.spring.ioc.logos.service.impl;

import cn.locusc.spring.ioc.logos.dao.AccountDao;
import cn.locusc.spring.ioc.logos.pojo.Account;
import cn.locusc.spring.ioc.logos.service.TransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Service;

/**
 * @author 应癫
 */
@Service("transferService")
@EnableAspectJAutoProxy
public class TransferServiceImpl implements TransferService {

    // @Autowired 按照类型注入
    // 如果按照类型无法唯一锁定对象, 可以结合@Qualifier
    // 指定具体的id
    @Autowired
    @Qualifier("accountDao")
    private AccountDao accountDao;

    @Override
    public void transfer(String fromCardNo, String toCardNo, int money) throws Exception {
        try {
            // 开启事务(关闭事务的自动提交)
            // ConnectionUtils.getInstance()
            //         .getCurrentThreadConnection().setAutoCommit(false);
            //TransactionManager.getInstance().beginTransaction();
            System.out.println("transfer..." + money);
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
            //TransactionManager.getInstance().commit();
        } catch (Exception e) {
            // 回滚事务
            // ConnectionUtils.getInstance()
            //         .getCurrentThreadConnection().rollback();
            //TransactionManager.getInstance().rollback();
            throw e;
        }
    }
}
