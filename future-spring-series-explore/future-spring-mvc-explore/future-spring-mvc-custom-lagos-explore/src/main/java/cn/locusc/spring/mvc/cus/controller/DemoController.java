package cn.locusc.spring.mvc.cus.controller;

import cn.locusc.spring.mvc.cus.framework.annotations.CusAutowired;
import cn.locusc.spring.mvc.cus.framework.annotations.CusController;
import cn.locusc.spring.mvc.cus.framework.annotations.CusRequestMapping;
import cn.locusc.spring.mvc.cus.service.IDemoService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@CusController
@CusRequestMapping("/demo")
public class DemoController {


    @CusAutowired
    private IDemoService demoService;


    /**
     * URL: /demo/query?name=lisi
     * @param request
     * @param response
     * @param name
     * @return
     */
    @CusRequestMapping("/query")
    public String query(HttpServletRequest request, HttpServletResponse response, String name) {
        return demoService.get(name);
    }
}
