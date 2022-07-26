package cn.locusc.mtia.chapter8.codelist;

import cn.locusc.mtia.utils.Tools;

import java.util.Random;

public class TaskWithException implements Runnable {

    final Random rnd;

    public TaskWithException(Random rnd) {
        super();
        this.rnd = rnd;
    }

    @Override
    public void run() {

        while(true){
            if(rnd.nextInt(100)<2){
                throw new RuntimeException("test");
            }
            Tools.randomPause(50);
        }

    }

}
