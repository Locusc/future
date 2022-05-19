package cn.locusc.rpc.netty.api;

import cn.locusc.rpc.netty.api.domain.User;

/**
 * 用户服务
 */
public interface IUserService {

    /**
     * 根据ID查询用户
     *
     * @param id
     * @return
     */
    User getById(int id);

}
