package cn.locusc.data.s.a.sort;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author Jay
 * 选择排序(依次对比,找到数组中最小的数与第一个数组交换,剩下的数字形成新的数组重复过程)
 * 最简单但是最没用的(时间复杂度O(n)并且不稳定)排序算法 有一定的优化空间
 * 如何计算时间和空间的复杂度
 * 算法的验证-随机数据生成器、对数器
 * 写算法程序的哲学
 *
 * 可优化项目
 * 1.一次遍历找到一个最小值和一个最大值 最小值往前放 最大值往后放
 * 2.一次遍历去多个数字 先内部比较出最小值 然后在外部比较最小值
 * 不稳定原因:
 * 两个相等的数,在排完顺序过后,相对顺序可能会变化
 * 5,3,5,2,1
 * 原本第一个5要在后面一个5前面,随着排序的交换,第一个5会到后面取
 * 2021/12/12
 */
public class SelectionSort {

    static Consumer<int []> tackle() {
        return arr -> {
            // 外层循环arr.length - 1 是因为最后一个数没有对比性了
            for (int i = 0; i < arr.length - 1; i++) {
                // 记录最小值位置 从0开始
                int minPos = i;

                for (int j = i + 1; j < arr.length; j++) {
                    // 如果当前内层循环的数字比最小位置的数小
                    // 改变最小位置为当前内存循环的位置

                    // 执行次数(n - 1) + (n - 2) + ... + 1
                    // 等差数列求和 n(n -1)/2
                    // ((n - 1) + 1)(n - 1) / 2
                    minPos = arr[minPos] < arr[j] ? minPos : j;
                }

                // System.out.println("最小值位置:" + minPos);

                // 先获取当前数组第一个数的位置
                int temp = arr[i];
                // 第一个数字等于最小位置的数字
                arr[i] = arr[minPos];
                // 最小位置的值等于原始第一个数的值
                arr[minPos] = temp;
            }
            // System.out.println(Arrays.toString(arr));
        };
    }

    /**
     * 1.一次遍历找到一个最小值和一个最大值 最小值往前放 最大值往后放
     * todo 中间数有问题
     */
    static Consumer<int []> tackleEnhance() {
        return arr -> {
            for (int i = 0; i < arr.length / 2; i++) {
                // 记录最小值位置 从0开始
                int minPos = i;
                // 记录最大值位置
                int maxPos = arr.length - (i + 1);

                for (int j = i + 1; j < arr.length - i; j++) {
                    minPos = arr[minPos] < arr[j] ? minPos : j;
                }
                int minValue = arr[i];
                arr[i] = arr[minPos];
                arr[minPos] = minValue;

                for (int j = arr.length - (i + 1); j > i; j--) {
                    maxPos = arr[maxPos] > arr[j] ? maxPos : j;
                }
                int maxValue = arr[arr.length - (i + 1)];
                arr[arr.length - (i + 1)] = arr[maxPos];
                arr[maxPos] = maxValue;
            }
           // System.out.print(Arrays.toString(arr));
        };
    }

    public static void main(String[] args) {
        DataChecker.check(tackleEnhance(), 100_000);
//        DataChecker.check(tackle(), 100_000);

    }

}
