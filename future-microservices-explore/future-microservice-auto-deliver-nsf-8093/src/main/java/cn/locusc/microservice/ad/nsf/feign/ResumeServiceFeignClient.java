package cn.locusc.microservice.ad.nsf.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "future-microservice-resume",path = "/resume")
public interface ResumeServiceFeignClient {

    @GetMapping("/openstate/{userId}")
    Integer findDefaultResumeState(@PathVariable("userId") Long userId);

}
