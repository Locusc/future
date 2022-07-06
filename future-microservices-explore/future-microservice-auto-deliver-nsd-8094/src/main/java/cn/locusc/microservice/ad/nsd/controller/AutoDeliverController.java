package cn.locusc.microservice.ad.nsd.controller;

import cn.locusc.microservice.ad.nsd.configuration.SentinelHandlers;
import cn.locusc.microservice.common.service.ResumeService;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auto/deliver")
public class AutoDeliverController {

    @Reference
    private ResumeService resumeService;

    @GetMapping("/checkState/{userId}")
    @SentinelResource(value = "findResumeOpenState",
            blockHandlerClass = SentinelHandlers.class,
            blockHandler = "handleException",
            fallbackClass = SentinelHandlers.class,
            fallback = "handleError")
    public Integer findResumeOpenState(@PathVariable Long userId) {
        return resumeService.findDefaultResumeByUserId(userId);
    }

}

