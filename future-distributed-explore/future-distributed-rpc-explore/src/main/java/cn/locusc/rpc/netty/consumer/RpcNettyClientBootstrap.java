package cn.locusc.rpc.netty.consumer;

import cn.locusc.rpc.netty.api.IUserService;
import cn.locusc.rpc.netty.api.domain.User;

/**
 * 测试类
 */
public class RpcNettyClientBootstrap {

    public static void main(String[] args) {
        IUserService userService = (IUserService) RpcNettyClientProxy.obtainProxy(IUserService.class);

        User user = userService.getById(2);

        System.out.println(user);
    }

}
