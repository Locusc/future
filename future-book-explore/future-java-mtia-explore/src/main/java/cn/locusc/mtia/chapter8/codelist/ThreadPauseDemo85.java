package cn.locusc.mtia.chapter8.codelist;

import cn.locusc.mtia.utils.Debug;
import cn.locusc.mtia.utils.Tools;

import java.util.Scanner;

/**
 * @author Jay
 * 暂停恢复demo
 *
 * 清单8-5展示了一个利用PauseControl 实现的线程的暂停与恢复的Demo。
 * 该Demo模拟一个奴隶( Slave)干活时每隔一段时间询问其主人( Master )能否允许其休息一下。
 * 奴隶只有在得到主人允许的情况下才能够休息，否则他必须继续干活!
 * 这里，奴隶停下手中的工作询问主人以及等到主人的许可后休息都是通过线程的暂挂来模拟的,而休息过后继续干活则是通过线程的恢复来模拟的。
 *
 * 2022/7/20
 */
public class ThreadPauseDemo85 {

    final static PauseControl84 pc = new PauseControl84();

    public static void main(String[] args) {
        final Runnable action = new Runnable() {
            @Override
            public void run() {
                Debug.info("Master,I'm working...");
                Tools.randomPause(300);
            }
        };
        Thread slave = new Thread() {
            @Override
            public void run() {
                try {
                    for (;;) {
                        pc.pauseIfNecessary(action);
                    }
                } catch (InterruptedException e) {
                    // 什么也不做
                }
            }
        };
        slave.setDaemon(true);
        slave.start();
        askOnBehaveOfSlave();
    }

    static void askOnBehaveOfSlave() {
        String answer;
        int minPause = 2000;
        try (Scanner sc = new Scanner(System.in)) {
            for (;;) {
                Tools.randomPause(8000, minPause);
                pc.requestPause();
                Debug.info("Master,may I take a rest now?%n");
                Debug.info("%n(1) OK,you may take a rest%n"
                        + "(2) No, Keep working!%nPress any other key to quit:%n");
                answer = sc.next();
                if ("1".equals(answer)) {
                    pc.requestPause();
                    Debug.info("Thank you,my master!");
                    minPause = 8000;
                } else if ("2".equals(answer)) {
                    Debug.info("Yes,my master!");
                    pc.proceed();
                    minPause = 2000;
                } else {
                    break;
                }
            }// for结束
        }// try结束
        Debug.info("Game over!");
    }

}
