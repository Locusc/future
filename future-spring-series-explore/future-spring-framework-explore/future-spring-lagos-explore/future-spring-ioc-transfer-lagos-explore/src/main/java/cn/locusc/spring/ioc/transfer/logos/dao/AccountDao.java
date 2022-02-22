package cn.locusc.spring.ioc.transfer.logos.dao;

import cn.locusc.spring.ioc.transfer.logos.pojo.Account;

/**
 * @author 应癫
 */
public interface AccountDao {

    Account queryAccountByCardNo(String cardNo) throws Exception;

    int updateAccountByCardNo(Account account) throws Exception;
}
