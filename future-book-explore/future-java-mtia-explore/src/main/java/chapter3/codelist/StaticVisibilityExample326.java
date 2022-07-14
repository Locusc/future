package chapter3.codelist;

import utils.Debug;
import utils.Tools;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jay
 * static关键字可见性保障示例
 *
 * 在如清单3-26所示的代码中,init方法所启动的线程至少可以看到语句①~语句③的操作结果，即该线程总是可以看到static字段taskConfig 的初始值。
 * 如果init方法被执行的时候(甚至是在此之前)其他线程执行了changeConfig方法，
 * 那么init方法中启动的线程能否读取到taskConfig 的相对新值也是没有保障的。这种情形下要保障可见性，我们仍然需要借助其他的线程同步机制。
 *
 * 对于引用型静态变量，static关键字还能够保障一个线程读取到该变量的初始值时，这个值所指向（引用）的对象已经初始化完毕。
 * 注意: static关键字仅仅保障读线程能够读取到相应字段的初始值，而不是相对新值。
 * 2022/7/12
 */
public class StaticVisibilityExample326 {

    private static Map<String, String> taskConfig;

    static {
        Debug.info("The class being initialized...");
        taskConfig = new HashMap<String, String>();// 语句①
        taskConfig.put("url", "https://github.com/Viscent");// 语句②
        taskConfig.put("timeout", "1000");// 语句③
    }

    public static void init() {
        // 该线程至少能够看到语句①～语句③的操作结果，而能否看到语句④～语句⑥的操作结果是没有保障的。
        Thread t = new Thread() {
            @Override
            public void run() {
                String url = taskConfig.get("url");
                String timeout = taskConfig.get("timeout");
                doTask(url, Integer.valueOf(timeout));
            }
        };
        t.start();
    }

    public static void changeConfig(String url, int timeout) {
        taskConfig = new HashMap<String, String>();// 语句④
        taskConfig.put("url", url);// 语句⑤
        taskConfig.put("timeout", String.valueOf(timeout));// 语句⑥
    }

    private static void doTask(String url, int timeout) {
        // 省略其他代码

        // 模拟实际操作的耗时
        Tools.randomPause(500);
    }

}
