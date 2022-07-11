package cn.locusc.seata.order.controller;

import cn.locusc.seata.order.entity.Order;
import cn.locusc.seata.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单控制层
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 下单
     */
    @RequestMapping("/add")
    public void addOrder(Long orderId,Integer goodsId, Integer num, Double money, String username) {
        Order order = new Order();
        order.setId(orderId);
        order.setGoodsId(goodsId);
        order.setNum(num);
        order.setMoney(money);
        order.setUsername(username);
        orderService.add(order);
    }

}
