package cn.locusc.microservice.auto.deliver.feign.fallback;

import cn.locusc.microservice.auto.deliver.feign.ResumeServiceFeignClient;
import feign.hystrix.FallbackFactory;

public class ResumeServiceFallbackFactory implements FallbackFactory<ResumeServiceFeignClient> {

    @Override
    public ResumeServiceFeignClient create(Throwable cause) {
        return resume -> -6;
    }
}
