package cn.locusc.spring.aop.logos.dao;

import cn.locusc.spring.aop.logos.pojo.Account;

/**
 * @author 应癫
 */
public interface AccountDao {

    Account queryAccountByCardNo(String cardNo) throws Exception;

    int updateAccountByCardNo(Account account) throws Exception;
}
