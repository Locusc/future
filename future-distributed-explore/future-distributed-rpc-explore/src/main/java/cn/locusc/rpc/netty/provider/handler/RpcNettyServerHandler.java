package cn.locusc.rpc.netty.provider.handler;

import cn.locusc.rpc.netty.api.common.RpcNettyRequest;
import cn.locusc.rpc.netty.api.common.RpcNettyResponse;
import cn.locusc.rpc.netty.provider.annotation.RpcNettyService;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.BeansException;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端业务处理类
 * 1.将标有@RpcService注解的bean缓存
 * 2.接收客户端请求
 * 3.根据传递过来的beanName从缓存中查找到对应的bean
 * 4.解析请求中的方法名称. 参数类型 参数信息
 * 5.反射调用bean的方法
 * 6.给客户端进行响应
 */
@Component
@ChannelHandler.Sharable
public class RpcNettyServerHandler extends SimpleChannelInboundHandler<String>
        implements ApplicationContextAware {

    private static final Map<String, Object> SERVICE_INSTANCE_MAP = new ConcurrentHashMap<>();

    /**
     * 1.将标有@RpcNettyService注解的bean缓存
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(RpcNettyService.class);

        if(beansWithAnnotation.isEmpty()) {
            return;
        }

        beansWithAnnotation.forEach((k, v) -> {
            int length = v.getClass().getInterfaces().length;

            if(length == 0) {
                throw new RuntimeException("service must be implementation interface");
            }

            // 默认取第一个接口作为缓存bean的名称
            String name = v.getClass().getInterfaces()[0].getName();
            SERVICE_INSTANCE_MAP.put(name, v);
        });
    }

    /**
     * 通道读取就绪事件
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        // 1.接收客户端请求- 将msg转化RpcRequest对象
        RpcNettyRequest rpcNettyRequest = JSON.parseObject(msg, RpcNettyRequest.class);
        RpcNettyResponse rpcNettyResponse = new RpcNettyResponse();
        rpcNettyResponse.setRequestId(rpcNettyRequest.getRequestId());

        try {
            // 业务处理
            Object handler = this.handler(rpcNettyRequest);
            rpcNettyResponse.setResult(handler);
        } catch (Exception e) {
            e.printStackTrace();
            rpcNettyResponse.setError(e.getMessage());
        }

        // 6.给客户端进行响应
        channelHandlerContext.writeAndFlush(JSON.toJSONString(rpcNettyResponse));
    }


    /**
     * 业务处理逻辑
     */
    public Object handler(RpcNettyRequest rpcNettyRequest) throws InvocationTargetException {
        // 3.根据传递过来的beanName从缓存中查找到对应的bean
        Object serviceBean = SERVICE_INSTANCE_MAP.get(rpcNettyRequest.getClassName());
        if (ObjectUtils.isEmpty(serviceBean)) {
            throw new RuntimeException("can not find service, " + rpcNettyRequest.getClassName());
        }

        // 4.解析请求中的方法名称. 参数类型 参数信息
        Class<?> serviceBeanClass = serviceBean.getClass();
        String methodName = rpcNettyRequest.getMethodName();
        Object[] parameters = rpcNettyRequest.getParameters();
        Class<?>[] parameterTypes = rpcNettyRequest.getParameterTypes();

        // 5.反射调用bean的方法- CGLIB反射调用
        FastClass fastClass = FastClass.create(serviceBeanClass);
        FastMethod method = fastClass.getMethod(methodName, parameterTypes);
        return method.invoke(serviceBean, parameters);
    }

}
