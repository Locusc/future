package cn.locusc.reactive.other.sse.controller;

import cn.locusc.reactive.other.sse.entity.LoanProcesses;
import cn.locusc.reactive.other.sse.entity.Temperature;
import cn.locusc.reactive.other.sse.service.LoanProcessesComponent;
import cn.locusc.reactive.other.sse.service.RxSseEmitter;
import cn.locusc.reactive.other.sse.service.TemperatureSensor;
import org.json.JSONObject;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@RestController
public class TemperatureController {

    private final Set<SseEmitter> clients = new CopyOnWriteArraySet<>();

    @Resource
    private TemperatureSensor temperatureSensor;

    @RequestMapping(value = "/temperature-stream-event", method = RequestMethod.GET)
    public SseEmitter events(HttpServletRequest request) {
        // ResponseBodyEmitter的子类,
        // 用于发送SSE(Server-Send Event): 服务器发送的事件
        SseEmitter emitter = new SseEmitter();

        // 设置超时时间
        // SseEmitter emitter = new SseEmitter(10000L);

        // 将当前发射器放到集合中
        clients.add(emitter);

        // 给当前发射器设置事件处理函数
        // 当异步请求超时的时候调用的代码
        // 该方法在异步请求超时的时候由容器线程调用
        emitter.onTimeout(() -> clients.remove(emitter));


        // 当异步请求结束的时候调用的代码.
        // 当超时或网络错误而终止异步请求处理的时候, 在容器线程调用该方法.
        // 该方法一般用于检车一个ResponseBodyEmitter实例已经无用了.
        emitter.onCompletion(() -> clients.remove(emitter));

        return emitter;
    }


    @Async // 异步事件处理
    @EventListener // 事件监听器, 该监听器只接收Temperature事件
    public void handleMessage(Temperature temperature) {
        System.out.println("监听到web的调度事件了 -- " + temperature);
        ArrayList<SseEmitter> deadEmitters = new ArrayList<>();

        // 遍历发射器集合
        clients.forEach(emitter -> {
            try {
                // 发射器发送温度对象, json类型
                JSONObject jsonObject = new JSONObject(temperature);
                String s1 = jsonObject.toString();
                emitter.send(s1);
            } catch (Exception ignore) {
                // 如果抛异常, 则将该发射器放到deadEmitters集合中
                deadEmitters.add(emitter);
            }
        });
        // 从clients中移除所有失效的发射器.
        clients.removeAll(deadEmitters);
    }

    @RequestMapping(value = "/temperature-stream", method = RequestMethod.GET)
    public SseEmitter getEvents(HttpServletRequest request) {
        // 用作控制器方法的返回值
        // 该返回值中封装了Subscriber，该Subscriber订阅了温度响应式流
        RxSseEmitter emitter = new RxSseEmitter();
        // 完成订阅
        temperatureSensor.temperatureStream().subscribe(emitter.getSubscriber());
        return emitter;
    }

    @Resource
    private LoanProcessesComponent loanProcessesComponent;

    /**
     * 建立消息连接
     * @return org.springframework.web.servlet.mvc.method.annotation.SseEmitter
     */
    @RequestMapping(value = "/loan-process-stream", method = RequestMethod.GET)
    public SseEmitter loanProcessStream(HttpServletRequest request) {
        // 用作控制器方法的返回值
        // 该返回值中封装了Subscriber, 该Subscriber订阅了订单流程响应式流
        RxSseEmitter<LoanProcesses> emitter = new RxSseEmitter<>();
        // 完成订阅
        loanProcessesComponent.loanProcessesObservable().subscribe(emitter.getSubscriber());
        return emitter;
    }

}
