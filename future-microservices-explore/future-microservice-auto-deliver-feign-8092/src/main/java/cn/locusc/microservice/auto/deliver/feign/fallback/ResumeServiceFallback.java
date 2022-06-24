package cn.locusc.microservice.auto.deliver.feign.fallback;

import cn.locusc.microservice.auto.deliver.feign.ResumeServiceFeignClient;

public class ResumeServiceFallback implements ResumeServiceFeignClient {

    @Override
    public Integer findDefaultResumeState(Long userId) {
        return -6;
    }

}
