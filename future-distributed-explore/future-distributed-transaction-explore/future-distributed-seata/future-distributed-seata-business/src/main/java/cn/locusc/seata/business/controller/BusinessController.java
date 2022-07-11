package cn.locusc.seata.business.controller;

import cn.locusc.seata.business.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    @GetMapping("/test1")
    public String test1() {
        businessService.sale(1, 10, 100d, "zhaoyang");
        return "success";
    }

    @GetMapping("/test2")
    public String test2() {
        businessService.sale(1, 101, 100d, "zhaoyang");
        return "success";
    }
}
