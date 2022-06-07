package cn.locusc.java.spi.service.impl;

import cn.locusc.java.spi.service.JavaSpiDemoService;

public class JavaSpiJayServiceImpl implements JavaSpiDemoService {

    @Override
    public String sendMessage() {
        return "LOCUSC";
    }

}
