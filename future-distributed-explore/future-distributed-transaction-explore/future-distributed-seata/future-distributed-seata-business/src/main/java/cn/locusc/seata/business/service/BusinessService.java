package cn.locusc.seata.business.service;

public interface BusinessService {

    void sale(Integer goodsId, Integer num, Double money, String username);
}
