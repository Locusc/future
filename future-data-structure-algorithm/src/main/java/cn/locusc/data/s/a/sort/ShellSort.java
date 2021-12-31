package cn.locusc.data.s.a.sort;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * @author Jay
 * 希尔排序(将数组以不断缩小的间隔取值并排序)
 * 采用不同的序列 有不同的时间复杂度
 * 1. 每次循环 序列长度 / 2
 * 2. knuth序列
 * 3. 质数
 * 2021/12/18
 */
public class ShellSort {

    public static Consumer<int[]> tackle() {
        return arr -> {
            for (int gap = arr.length >> 1; gap > 0; gap /= 2) {
                base(arr, gap);
            }
        };
    }

    public static Consumer<int[]> tackleKnuth() {
        return arr -> {
            int h = 1;
            while (h <= arr.length / 3) {
                h = 3 * h + 1;
            }
            for (int gap = h ; gap > 0; gap = (gap - 1) / 3) {
                base(arr, gap);
            }
        };
    }

    private static void base(int[] arr, int gap) {
        for (int i = gap; i < arr.length; i++) {
            for (int j = i; j >= gap; j-=gap) {
                if(arr[j] < arr[j - gap]) {
                    int temp = arr[j];
                    arr[j] = arr[j - gap];
                    arr[j - gap] = temp;
                }
            }
        }
    }

    public static void main(String[] args) {
        // first one [1, 6, 2, 3, 5, 12, 8, 4, 9, 13, 11, 7, 10, 15, 14]
        int[] arr = {9,6,11,3,5,12,8,7,10,15,14,4,1,13,2};

//        Consumer<int[]> tackle = tackle();
//        tackle.accept(arr);

        Consumer<int[]> tackleKnuth = tackleKnuth();
        tackleKnuth.accept(arr);

        System.out.println(Arrays.toString(arr));
        DataChecker.check(tackleKnuth(), 100_000, 1, true);
    }

}
