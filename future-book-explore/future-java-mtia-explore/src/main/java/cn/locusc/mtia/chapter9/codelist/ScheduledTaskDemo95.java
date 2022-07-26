package cn.locusc.mtia.chapter9.codelist;

import cn.locusc.mtia.utils.Debug;
import cn.locusc.mtia.utils.Tools;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jay
 * ScheduledExecutorService使用Demo
 *
 * 9.4计划任务
 * 在有些情况下，我们可能需要事先提交一个任务，这个任务并不是立即被执行的，而是要在指定的时间或者周期性地被执行，
 * 这种任务就被称为计划任务（Scheduled Task )。典型的计划任务包括清理系统垃圾数据、系统监控、数据备份等。
 *
 * ExecutorService接口的子类ScheduledExecutorService接口定义了一组方法用于执行计划任务。
 * ScheduledExecutorService接口的默认实现类是 java.util.concurrent.ScheduledThreadPoolExecutor类，
 * 它是 ThreadPoolExecutor的一个子类。Executors除了提供创建ExecutorService实例的便捷工厂方法之外,
 * 它还提供了两个静态工厂方法用于创建ScheduledExecutorScrvice实例:
 * public static ScheduledExecutorService newScheduledThreadPool(int corePoolsize)
 * public static scheduledExecutorService newScheduledThreadPool(int corePoolsize,ThreadFactory threadFactory)
 *
 * ScheduledExecutorService接口定义的方法按其功能可分为以下两种。·延迟执行提交的任务。这包括以下两个方法:
 * <V> ScheduledFuture<V> schedule(callable<V> callable,long delay,TimeUnit unit)
 * scheduledFuture<?> schedule (Runnable command,long delay,TimeUnit unit)
 * 上述两个方法使得我们可以采用Callable实例或者 Runnable 实例来表示任务。
 * delay参数和 unit参数一起用来表示被提交的任务自其提交的那一刻到其开始执行之间的时间差，即延时。
 * 上述方法的返回值类型ScheduledFuture继承自Future接口，因此我们也可以利用上述方法的返回值来获取所提交的计划任务的处理结果。
 *
 * 周期性地执行提交的任务。这包括以下两个方法:
 * scheduledFuture<?> scheduleAtFixedRate(Runnable command,long initialDelay,long period,Timeunit unit)
 *
 * 约定:同一个任务任意两次执行的开始时间之间的时间差被称为该任务的执行周期，记为Interval。
 * 一个任务从其开始执行到其执行结束所需的时间被称为该任务的耗时，简称耗时，记为Execution Time。
 *
 * 从该方法的名字上看,它能够以固定的频率不断地执行command参数所指定的任务。initialDelay参数和unit参数一起指定了一个时间偏移,
 * 任务首次执行的开始时间就是任务提交时间加上这个偏移。实际上，提交给scheduleAtFixedRate方法执行的计划任务，其执行周期并不一定是固定的，
 * 它会同时受Execution Time和 period 的影响——Interval =max(Execution Time,period)，如图9-3所示:
 * 如果任务的每次执行总是能够在period 指定的时间跨度内完成时，那么该任务的执行周期就是period指定的时间跨度，
 * 此时任务的执行周期是恒定的;如果该任务的某些次执行，其执行耗时超过了period 指定的时间跨度，
 * 那么该任务的执行周期就会变得不固定——有时其执行周期等于period，有时却大于period。
 * cn.locusc.mtia.chapter9\images\9-3-scheduleAtFixedRate方法执行任务的周期示意图.jpg
 *
 *
 * scheduledFuture<?> schedulewithFixedDelay(Runnable command,long initialDelay, long delay ,TimeUnit unit)
 * 其中,initialDelay参数和unit参数一起指定了一个时间偏移，任务首次执行的开始时间就是任务提交时间加上这个偏移。
 * 提交给scheduleWithFixedDelay方法执行的计划任务的执行周期Interval = Execution Time + delay，
 * 其中 delay是一个固定值，因此任务的执行周期实际上也不是固定的而是随Execution Time 的变化而变化,如图9-4所示。
 * cn.locusc.mtia.chapter9\images\9-4-scheduleWithFixedDelay方法执行任务的周期示意图.jpg
 *
 * 由于同一个任务每次执行的耗时可能都不同，它既可能变大也可能变小。因此，Execution Time值有可能比 period或者delay 的参数值还大。
 * 这就导致了同一个任务的执行周期往往不是固定的,如清单9-5所示的 Demo能够展示这一点。
 *
 * 在Execution Time始终不长于delay或者period所代表的时间的情况下，
 * scheduleAtFixedRate和 scheduleWithFixedDelay能够实现同样的效果——按照固定的时间间隔不断地执行任务。
 * 例如，使用如下命令运行清单9-5所示的程序:
 * java cn.locusc.mtia.chapter9.codelist.ScheduledTaskDemo95 1000 1000
 * 上述命令的输出类似如下:
 * [2016-06-23 20:29:33.959] [ INFO] [pool-1-thread-2] : schedulewithFixedDelay run-1
 * [2016-06-23 20:29:33.959][INF0] [pool-1-thread-1] :scheduleAtFixedRate run-1
 * [2016-06-23 20:29:35.878 ] [INFO] [pool-1-thread-1] : scheduleAtFixedRate run-2
 * [2016-06-23 20:29:35.962][INFO] [pool-1-thread-2] : schedulewithFixedDelay run-2
 * [2016-06-23 20:29:37.878] [ INFo] [pool-1-thread-1] : scheduleAtFixedRate run-3
 * [2016-06-23 20:29:37.962][INFO] [pool-1-thread-2] : scheduleWithFixedDelay run-3
 * [2016-06-23 20:29:39.878] [ INFo] [pool-1-thread-1] : scheduleAtFixedRate run-4
 * [2016-06-23 20:29:39.963] [INFO] [pool-1-thread-2 ] : schedulewithFixedDelay run-4
 * 可见，两个计划任务都是每2秒执行一次。
 *
 * 在Execution Time长于delay或者period所代表的时间的情况下,
 * scheduleAtFixedRate和scheduleWithFixedDelay都无法保证计划任务以固定的周期被执行。例如，使用如下命令运行如清单9-5所示的程序:
 * java cn.locusc.mtia.chapter9.codelist.ScheduledTaskDemo95 1000 3000
 * 上述命令的输出类似如下:
 * [2016-06-23 20:06:19.417][INFo] [poo1-1-thread-1] : schedulewithFixedDelay run-1
 * [2016-06-23 20:06:19.650][INFO] [pool-l-thread-2] ; scheduleAtFixedRate run-1
 * [2016-06-23 20:06:21.227] [ INFO][pool-1-thread-2] : scheduleAtFixedRate run-2
 * [2016-06-23 20:06:22.774] [INFO] [pool-1-thread-1] : schedulewithFixedDelay run-2
 * [2016-06-23 20:06:23.832] [ INFO] [pool-1-thread-2] : scheduleAtFixedRate run-3
 * [2016-06-23 20:06:25.378][INFO] [pool-1-thread-1 ] : schedulewithFixedDelay run-3
 * [2016-06-23 20:06:26.097] [ INFO] [pool-1-thread-2] : scheduleAtFixedRate run-4
 * [2016-06-23 20:06:27.214][INFO] [pool-1-thread-2] : scheduleAtFixedRate run-5
 * [2016-06-23 20:06:28.863][INFO][poo1-1-thread-1] : schedulewithFixedDelay run-4
 *
 * 可见，两个任务的执行周期分别在1~3和2~4之间变化。从以上输出中还可以看出，
 * 一个任务的执行耗时超过period或者delay所表示的时间只会导致该任务的下一次执行时间被相应地推迟，
 * 而不会导致该任务在同一个时间内被运行多次（并发执行)。
 * 注意: 一个任务的执行耗时超过period或者delay所表示的时间只会导致该任务的下一次执行时间被相应地推迟，而不会导致该任务被并发执行。
 * 2022/7/21
 */
public class ScheduledTaskDemo95 {

    static ScheduledExecutorService ses = Executors.newScheduledThreadPool(2);

    public static void main(String[] args) throws InterruptedException {
        final int argc = args.length;
        // 任务执行最大耗时
        int maxConsumption;
        // 任务执行最小耗时
        int minConsumption;
        if (argc >= 2) {
            minConsumption = Integer.valueOf(args[0]);
            maxConsumption = Integer.valueOf(args[1]);
        } else {
            maxConsumption = minConsumption = 1000;
        }

        ses.scheduleAtFixedRate(new SimulatedTask(minConsumption, maxConsumption,
                "scheduleAtFixedRate"), 0, 2, TimeUnit.SECONDS);
        ses.scheduleWithFixedDelay(new SimulatedTask(minConsumption, maxConsumption,
                "scheduleWithFixedDelay"), 0, 1, TimeUnit.SECONDS);
        Thread.sleep(20000);

        ses.shutdown();
    }

    static class SimulatedTask implements Runnable {
        private String name;
        // 模拟任务执行耗时
        private final int maxConsumption;
        private final int minConsumption;
        private final AtomicInteger seq = new AtomicInteger(0);

        public SimulatedTask(int minConsumption, int maxConsumption, String name) {
            this.maxConsumption = maxConsumption;
            this.minConsumption = minConsumption;
            this.name = name;
        }

        @Override
        public void run() {
            try {
                // 模拟任务执行耗时
                Tools.randomPause(maxConsumption, minConsumption);
                Debug.info(name + " run-" + seq.incrementAndGet());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }// run结束
    }

}
