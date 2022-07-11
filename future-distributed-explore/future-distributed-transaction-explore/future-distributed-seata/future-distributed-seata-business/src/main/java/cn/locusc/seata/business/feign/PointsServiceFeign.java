package cn.locusc.seata.business.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("future-distributed-seata-points")
public interface PointsServiceFeign {

    @GetMapping("/points/increase")
    void increase(@RequestParam("username") String username, @RequestParam("points") Integer points);

}
