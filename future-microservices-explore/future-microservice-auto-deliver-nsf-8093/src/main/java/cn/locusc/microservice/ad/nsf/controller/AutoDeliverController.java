package cn.locusc.microservice.ad.nsf.controller;

import cn.locusc.microservice.ad.nsf.configuration.SentinelHandlers;
import cn.locusc.microservice.ad.nsf.feign.ResumeServiceFeignClient;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/autodeliver")
public class AutoDeliverController {

    @Autowired
    private ResumeServiceFeignClient resumeServiceFeignClient;

    /**
     *  @SentinelResource注解类似于Hystrix中的@HystrixCommand注解
     */
    @GetMapping("/checkState/{userId}")
    @SentinelResource(value = "findResumeOpenState",
            blockHandlerClass = SentinelHandlers.class,
            blockHandler = "handleException",
            fallbackClass = SentinelHandlers.class,
            fallback = "handleError")
    public Integer findResumeOpenState(@PathVariable Long userId) {
        // 模拟降级：
        /*try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        // 模拟降级：异常比例
        //int i = 1/0;
        return resumeServiceFeignClient.findDefaultResumeState(userId);
    }

}
