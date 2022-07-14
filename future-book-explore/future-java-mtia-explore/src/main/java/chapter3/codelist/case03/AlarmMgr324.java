package chapter3.codelist.case03;

import utils.Debug;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Jay
 * AtomicBoolean运用实例代码
 *
 * AtomicBoolean变量initializating 用于表示告警管理器初始化（即创建并启动告警上报线程）的状态。
 * initializating 内部值为true 表示正在初始化(或已初始化完毕)，false表示未开始初始化。
 * AlarmMgr.init()在创建(并启动)告警上报线程前会检查initializating的内部值:若initializating内部值为false，
 * 则将其置为true以表示当前线程即将执行初始化;若initializating 内部值为true，则当前线程直接从 AlarmMgr.init()返回。
 * 显然，在多线程环境下这个将initializating 内部值从 false调整为true 的过程是一个check-then-act操作，
 * 若用锁来保障该操作的原子性，那么AlarmMgr.init()看起来会像这样:
 *
 * public void init () {
 *  synchronized (this) {
 *      if (initInProgress) {
 *          return;
 *      }
 *      initInProgress = true;
 * }
 * Debug.info ("initializating. .. ");//创建并启动工作者线程
 * new Thread(this).start();
 *
 * 2022/7/12
 */
public enum AlarmMgr324 implements Runnable {

    INSTANCE;

    private final AtomicBoolean initializing = new AtomicBoolean(false);

    AlarmMgr324() {
        // 什么也不做
    }

    public void init() {
        // 使用AtomicBoolean的CAS操作确保工作者线程只会被创建（并启动）一次
        if (initializing.compareAndSet(false, true)) {
            Debug.info("initializing...");
            // 创建并启动工作者线程
            new Thread(this).start();
        } else {
            Debug.info("it was initialized...");
        }
    }

    public int sendAlarm(String message) {
        int result = 0;
        // ...
        return result;
    }

    @Override
    public void run() {
        // ...
    }

}
