package cn.locusc.spring.mvc.cus.service.impl;


import cn.locusc.spring.mvc.cus.framework.annotations.CusService;
import cn.locusc.spring.mvc.cus.service.IDemoService;

@CusService("demoService")
public class DemoServiceImpl implements IDemoService {
    @Override
    public String get(String name) {
        System.out.println("service 实现类中的name参数：" + name) ;
        return name;
    }
}
