package cn.locusc.seata.business.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("future-distributed-seata-storage")
public interface StorageServiceFeign {

    @GetMapping("/storage/decrease")
    void decrease(@RequestParam("goodsId") Integer goodsId, @RequestParam("quantity") Integer quantity);

}
