package cn.locusc.data.s.a.sort;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

/**
 * @author Jay
 * 验证算法-对数器
 * 1.肉眼观察
 * 2.产生足够多的随机样本
 * 3.用确定正确的算法计算样本结果
 * 4.对比被验证算法的结果
 * 2021/12/12
 */
public class DataChecker {

    public static int [] generateRandomArray(int length) {
        Random random = new Random();
        int[] arr = new int[length];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = random.nextInt(length);
        }
        return arr;
    }

    public static void check(Consumer<int[]> tackle, int length) {
        int[] arr = generateRandomArray(length);
        int[] arr2 = new int[arr.length];
        System.arraycopy(arr, 0, arr2, 0, arr.length);

        Arrays.sort(arr);

        long start = System.nanoTime();
        tackle.accept(arr2);
        System.out.println("tackle use: "
                + ((System.nanoTime() - start) / 1_000_000) + " msecs");

        boolean same = true;
        for (int i = 0; i < arr2.length; i++) {
            if (arr[i] != arr2[i]) {
                System.out.println("arr:" + arr[i] + " arr2:" + arr2[i]);
                same = false;
                break;
            }
        }

        if(!same) {
            System.out.println(Arrays.toString(arr2));
        }

        System.out.println(same ? "RIGHT" : "WRONG");

    }

}
