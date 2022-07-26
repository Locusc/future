package cn.locusc.mtia.chapter9.codelist.func;

import cn.locusc.mtia.utils.Debug;
import cn.locusc.mtia.utils.Tools;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PeriodicTaskResultHandlingDemoFunc {

    final static ScheduledExecutorService ses = Executors.newScheduledThreadPool(2);

    final static Random rnd = new Random();

    final static Logger LOGGER = Logger.getAnonymousLogger();

    private static Integer pingHostMf() {
        // 模拟实际操作耗时
        Tools.randomPause(2000);
        // 模拟的探测结果码
        Integer r = Integer.valueOf(rnd.nextInt(4));
        return r;
    }

    private static Supplier<Integer> pingHostFunc() {
        return () -> {
            // 模拟实际操作耗时
            Tools.randomPause(2000);
            // 模拟的探测结果码
            Integer r = Integer.valueOf(rnd.nextInt(4));
            return r;
        };
    }

    protected static void saveToDatabaseFuncMf(Integer r, String h) {
        Debug.info(h + " status:" + String.valueOf(r));
        // 省略其他代码
    }

    protected static BiConsumer<Integer, String> saveToDatabaseFunc() {
        return (b, c) -> {
            Debug.info(c + " status:" + String.valueOf(b));
            // 省略其他代码
        };
    }

    protected static void onErrorMf(Exception e) {
        LOGGER.log(Level.SEVERE, "AsyncTask[" + PeriodicTaskResultHandlingDemoFunc.class + "] failed.", e);
    }

    protected static Consumer<Exception> onErrorFunc() {
        return e -> LOGGER.log(Level.SEVERE, "AsyncTask[" + PeriodicTaskResultHandlingDemoFunc.class + "] failed.", e);
    }


    public static Supplier<AsyncTaskFunction<Integer, String>> function() {
        return () -> new AsyncTaskFunction<Integer, String>()
                .setAction("192.168.0.41")
                .setOnCall(pingHostFunc())
                .setOnResult(saveToDatabaseFunc())
                .setOnError(onErrorFunc());
    }

    public static Supplier<AsyncTaskFunction<Integer, String>> methodReference() {
        return () -> new AsyncTaskFunction<Integer, String>()
                .setAction("192.168.0.41")
                .setOnCall(PeriodicTaskResultHandlingDemoFunc::pingHostMf)
                .setOnResult(PeriodicTaskResultHandlingDemoFunc::saveToDatabaseFuncMf)
                .setOnError(PeriodicTaskResultHandlingDemoFunc::onErrorMf);
    }

    public static void main(String[] args) {
        ses.scheduleAtFixedRate(function().get(), 0, 3, TimeUnit.SECONDS);

        Tools.delayedAction(
                "The ScheduledExecutorService will be shutdown",
                ses::shutdown,
                60
        );
    }


}
