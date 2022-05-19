package cn.locusc.rpc.netty.api.common;

import lombok.Data;

/**
 * 封装的响应对象
 */
@Data
public class RpcNettyResponse {

    /**
     * 响应ID
     */
    private String requestId;

    /**
     * 错误信息
     */
    private String error;

    /**
     * 返回的结果
     */
    private Object result;

}
