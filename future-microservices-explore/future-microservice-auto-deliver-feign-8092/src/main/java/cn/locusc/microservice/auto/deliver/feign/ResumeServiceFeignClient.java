package cn.locusc.microservice.auto.deliver.feign;

import cn.locusc.microservice.auto.deliver.feign.fallback.ResumeServiceFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Jay
 * 原来: http://lagou-service-resume/resume/openstate/ + userId;
 * @FeignClient表明当前类是一个Feign客户端
 * value指定该客户端要请求的服务名称（登记到注册中心上的服务提供者的服务名称）
 * 2022/6/24
 */
@FeignClient(value = "future-service-resume", path = "/resume", fallbackFactory = ResumeServiceFallbackFactory.class)
//@RequestMapping("/resume")
public interface ResumeServiceFeignClient {

    /**
     * // Feign要做的事情就是，拼装url发起请求
     * // 我们调用该方法就是调用本地接口方法，那么实际上做的是远程请求
     */
    @GetMapping("/openstate/{userId}")
    Integer findDefaultResumeState(@PathVariable("userId") Long userId);

}
