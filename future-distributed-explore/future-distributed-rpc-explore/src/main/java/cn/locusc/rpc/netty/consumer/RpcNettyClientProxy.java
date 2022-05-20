package cn.locusc.rpc.netty.consumer;

import cn.locusc.rpc.netty.api.common.RpcNettyRequest;
import cn.locusc.rpc.netty.api.common.RpcNettyResponse;
import com.alibaba.fastjson.JSON;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * 客户端代理类-创建代理对象
 * 1.封装request请求对象
 * 2.创建RpcClient对象
 * 3.发送消息
 * 4.返回结果
 */
public class RpcNettyClientProxy implements InvocationHandler {

    /**
     * 获取代理对象
     */
    public static Object obtainProxy(Class<?> serviceClass) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Class<?>[] classes = new Class[]{serviceClass};
        return Proxy.newProxyInstance(contextClassLoader, classes, new RpcNettyClientProxy());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 1.封装request请求对象
        RpcNettyRequest rpcNettyRequest = new RpcNettyRequest()
            .setRequestId(UUID.randomUUID().toString())
            .setClassName(method.getDeclaringClass().getName())
            .setMethodName(method.getName())
            .setParameterTypes(method.getParameterTypes())
            .setParameters(args);

        // 2.创建RpcClient对象
        RpcNettyClient rpcNettyClient = new RpcNettyClient("127.0.0.1", 8899);


        try {
            // 3.发送消息
            Object responseMsg = rpcNettyClient.send(JSON.toJSONString(rpcNettyRequest));

            RpcNettyResponse rpcNettyResponse = JSON.parseObject(
                    responseMsg.toString(),
                    RpcNettyResponse.class
            );

            if(!StringUtils.isEmpty(rpcNettyResponse.getError())) {
                throw new RuntimeException(rpcNettyResponse.getError());
            }

            // 4.返回结果
            Object result = rpcNettyResponse.getResult();
            return JSON.parseObject(JSON.toJSONString(result), method.getReturnType());
        } catch (Exception e) {
            throw e;
        } finally {
            rpcNettyClient.close();
        }
    }

}
