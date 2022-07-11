package cn.locusc.seata.business.service.impl;

import cn.locusc.seata.business.feign.OrderServiceFeign;
import cn.locusc.seata.business.feign.PointsServiceFeign;
import cn.locusc.seata.business.feign.StorageServiceFeign;
import cn.locusc.seata.business.service.BusinessService;
import cn.locusc.seata.business.utils.IdWorker;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 业务逻辑
 */
@Service
public class BusinessServiceImpl implements BusinessService {

    @Autowired
    private OrderServiceFeign orderServiceFeign;

    @Autowired
    private PointsServiceFeign pointsServiceFeign;

    @Autowired
    private StorageServiceFeign storageServiceFeign;

    @Autowired
    private IdWorker idWorker;

    /**
     * 商品销售
     * @param goodsId  商品id
     * @param num      销售数量
     * @param username 用户名
     * @param money    金额
     */
    // @Transactional
    @GlobalTransactional(name = "sale", timeoutMills = 30000, rollbackFor = Throwable.class)
    public void sale(Integer goodsId, Integer num, Double money, String username) {
        //创建订单
        orderServiceFeign.addOrder(idWorker.nextId(), goodsId, num, money, username);
        //增加积分
        pointsServiceFeign.increase(username, (int) (money / 10));
        //扣减库存
        storageServiceFeign.decrease(goodsId, num);
    }

}
