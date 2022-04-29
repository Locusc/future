package cn.locusc.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class SimpleQuartzDemo {

    // 1、创建任务调度器
    public static Scheduler createScheduler() throws SchedulerException {
        StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();
        return stdSchedulerFactory.getScheduler();
    }

    // 2、创建一个任务
    public static JobDetail createJob() {
        JobBuilder jobBuilder = JobBuilder.newJob(SimpleQuartzExecute.class);
        jobBuilder.withIdentity("jobName", "myJob");
        return jobBuilder.build();
    }

    /**
     * 3、创建作业任务时间触发器
     * cron表达式由七个位置组成，空格分隔
     * 1、Seconds（秒）  0~59
     * 2、Minutes（分）  0~59
     * 3、Hours（小时）  0~23
     * 4、Day of Month（天）1~31,注意有的月份不足31天
     * 5、Month（月） 0~11,或者 JAN,FEB,MAR,APR,MAY,JUN,JUL,AUG,SEP,OCT,NOV,DEC
     * 6、Day of Week(周)  1~7,1=SUN或者  SUN,MON,TUE,WEB,THU,FRI,SAT
     * 7、Year（年）1970~2099  可选项
     * 示例：
     * 0 0 11 * * ? 每天的11点触发执行一次
     * 0 30 10 1 * ? 每月1号上午10点半触发执行一次
     */
    public static Trigger createTrigger() {
        return TriggerBuilder.newTrigger()
                .withIdentity("triggerName", "myTrigger")
                .startNow()
                .withSchedule(CronScheduleBuilder.cronSchedule("*/2 * * * * ?"))
                .build();
    }

    /**
     * main函数中开启定时任务
     */
    public static void main(String[] args) throws SchedulerException {
        Scheduler scheduler = SimpleQuartzDemo.createScheduler();
        JobDetail job = SimpleQuartzDemo.createJob();
        Trigger trigger = SimpleQuartzDemo.createTrigger();
        scheduler.scheduleJob(job, trigger);
        scheduler.start();
    }

}
