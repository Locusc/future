package cn.locusc.java8action.chapter16Future;

import cn.locusc.java8action.domain.Apple;

import java.util.Arrays;

/**
 * @author Jay
 * 结论以及Java的未来
 *  Java 8的新特性以及其对编程风格颠覆性的影响
 *  由Java 8萌生的一些尚未成熟的编程思想
 *  Java 9以及Java 10可能发生的变化
 * 2021/12/5
 */
public class ActionClass {

    public static void main(String[] args) {
        Apple apple = new Apple();
        apple.setColor("lk");

        Apple a = apple;

        a = null;

        System.out.println(apple);

        int[] test = {1,2,3};

        int [] add = test;

        add[1] = 10;

        System.out.println(Arrays.toString(test));
    }

}
