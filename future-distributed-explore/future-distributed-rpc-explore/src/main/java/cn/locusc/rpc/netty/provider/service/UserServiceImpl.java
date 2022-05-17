package cn.locusc.rpc.netty.provider.service;

import cn.locusc.rpc.netty.api.IUserService;
import cn.locusc.rpc.netty.api.domain.User;
import cn.locusc.rpc.netty.provider.annotation.RpcNettyService;

import java.util.HashMap;
import java.util.Map;

@RpcNettyService
public class UserServiceImpl implements IUserService {

    Map<Object, User> userMap = new HashMap<>();

    @Override
    public User getById(int id) {
        if (userMap.size() == 0) {
            User user1 = new User();
            user1.setId(1);
            user1.setName("张三");
            User user2 = new User();
            user2.setId(2);
            user2.setName("李四");
            userMap.put(user1.getId(), user1);
            userMap.put(user2.getId(), user2);
        }
        return userMap.get(id);
    }

}
