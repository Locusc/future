package cn.locusc.mtia.chapter9.codelist;

import cn.locusc.mtia.utils.Debug;
import cn.locusc.mtia.utils.Tools;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Jay
 * 周期性任务的执行结果处理 Demo
 *
 * 延迟执行的任务最多只会被执行一次，因此我们利用schedule 方法的返回值( ScheduledFuture实例）便能获取这种计划任务的执行结果、
 * 执行过程中抛出的异常以及取消任务的执行。
 *
 * 周期性执行的任务会不断地被执行，直到任务被取.消或者相应的ScheduledExecutorService实例被关闭。
 * 因此 , scheduleAtFixedRate方法、scheduleWithFixedDelay方法的返回值( ScheduledFuture<?>)能够取消相应的任务，
 * 但是它无法获取计划任务的一次或者多次的执行结果6。如果我们需要对周期性执行的计划任务的执行结果进行处理，
 * 那么可以考虑使用如清单9-4所示的异步任务类AsyncTask 来表示计划任务。
 *
 * 清单9-6使用AsyncTask模拟了这样一个周期性任务:每隔一段时间（比如3秒)检测一下当前主机与指定的目标主机之间的网络连通性（比如使用ping命令检测)，
 * 并将检测的结果记录到数据库之中。这里，我们在 AsyncTask的匿名子类的call方法中实现检测的逻辑，
 * 并在 onResult方法中将检测的结果记录到数据库之中。可见，这个计划任务的任务执行逻辑和结果处理逻辑是异步进行的。
 *
 * 6周期性执行的任务会不断地被执行,因此获取这种计划任务任意一次执行的结果意义不大,而获取全
 * 部次执行的结果又有些困难——只有当相应的计划任务不会再被执行的情况下我们才能够获取这样的结果。
 *
 * 提交给ScheduledExecutorService执行的计划任务在其执行过程中如果抛出未捕获的异常( Uncaught Exception)，那么该任务后续就不会再被执行。
 * 即使我们在创建ScheduledExecutorService实例的时候指定一个线程工厂,并使线程工厂为其创建的线程关联一个 UncaughtExceptionHandler ,
 * 当计划任务抛出未捕获异常的时候该UncaughtExceptionHandler也不会被 ScheduledExecutorService实例调用。
 * 因此，我们必须确保周期性执行的任务在其执行过程中不会抛出任何未捕获异常。
 *
 * 注意: 提交给ScheduledExecutorService执行的计划任务在其执行过程中如果抛出未捕获的异常（Uncaught Exception )，那么该任务后续就不会再被执行。
 * 2022/7/21
 */
public class PeriodicTaskResultHandlingDemo96 {

    final static ScheduledExecutorService ses = Executors.newScheduledThreadPool(2);

    public static void main(String[] args) {
        final String host = args[0];
        final AsyncTask94<Integer> asyncTask = new AsyncTask94<Integer>() {

            final Random rnd = new Random();
            final String targetHost = host;

            @Override
            protected void onResult(Integer result) {
                // 将结果保存到数据库
                saveToDatabase(result);
            }

            @Override
            public Integer call() throws Exception {
                return pingHost();
            }

            private Integer pingHost() throws Exception {
                // 模拟实际操作耗时
                Tools.randomPause(2000);
                // 模拟的探测结果码
                Integer r = Integer.valueOf(rnd.nextInt(4));
                return r;
            }

            private void saveToDatabase(Integer result) {
                Debug.info(targetHost + " status:" + String.valueOf(result));
                // 省略其他代码
            }

            @Override
            public String toString() {
                return "Ping " + targetHost + "," + super.toString();
            }
        };

        ses.scheduleAtFixedRate(asyncTask, 0, 3, TimeUnit.SECONDS);

        Tools.delayedAction("The ScheduledExecutorService will be shutdown", new Runnable() {
            @Override
            public void run() {
                ses.shutdown();
            }
        }, 60);
    }

}
