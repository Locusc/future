package cn.locusc.data.s.a.sort;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * @author Jay
 * 归并排序(将数组不断递归拆分到足够小, 比较后在进行合并)
 * java Array.sort() 如果基础数据类型使用双轴快排, 对象使用TimSort
 * 改进版的归并排序, 固定拆分大小, 先使用二分插入排序小的空间, 最后两两归并
 * BinarySort 二分插入
 * 2021/12/18
 */
public class MergeSort {

    private static long fun(int n) {
        return func(1, n);
    }

    private static long func(int acc, int n) {
        return n == 1 ? acc : func(acc * n, n - 1);
    }

    private static long func(int n) {
        return n == 1 ? 1 : n * func(n - 1);
    }


    private static Consumer<int []> tackle() {
        return arr -> {
            // 中间位置
            int middle = arr.length >> 1;

            // 前数组起始位
            int i = 0;

            // 后数据起始位
            // 当arr当都为偶数次 并且i<=middle时 最终i多取一位
            // 导致k被多占用一位 导致k和剩下的j长度不同数组从而下标越界
            // 所以为偶数此不能使用等于
            int j = middle + 1;

            // 存储空间起始位
            int k = 0;

            int[] temp = new int[arr.length];

            while (i <= middle && j < arr.length) {
                if(arr[i] <= arr[j]) {
                    temp[k] = arr[i];
                    i++;
                } else {
                    temp[k] = arr[j];
                    j++;
                }
                k++;
            }

            while (i <= middle) temp[k++] = arr[i++];
            while (j < arr.length) temp[k++] = arr[j++];

            System.out.println(Arrays.toString(temp));
        };
    }

    private static Consumer<int []> tackleEnhance(int left, int right) {
        return arr -> sort(arr, left, right);
    }

    private static void sort(int[] arr, int left, int right) {
        if(left == right) {
            return;
        }
        int middle = left + (right - left) / 2;

        //sort(arr, left, middle);
        // sort(arr, middle + 1, right);

        merge(arr, left, middle + 1, right);
    }


    static void merge(int[] arr, int leftPrt, int rightPrt, int rightBound) {
        int middle = rightPrt - 1;

        int[] temp = new int[rightBound - leftPrt + 1];

        int i = leftPrt;
        int j = rightPrt;
        int k = 0;

        // 比较两个分区的值
        // 比较后放入新的数组
        while (i <= middle && j <= rightBound) {
            if(arr[i] <= arr[j]) {
                temp[k] = arr[i];
                i++;
            } else {
                temp[k] = arr[j];
                j++;
            }
            k++;
        }

        // 两边长度不相等时, 可能有没有取完的值, 那么直接复制到新的数组
        while (i <= middle) temp[k++] = arr[i++];
        while (j <= rightBound) temp[k++] = arr[j++];

        if (temp.length >= 0) System.arraycopy(temp, 0, arr, leftPrt, temp.length);
    };

    public static void main(String[] args) {

        int[] arr = {9,3,1,4,6,8,7,5,2};
        //int[] arr = {1,4,7,8,3,6,9};

        // tackle().accept(arr);

        // merge(arr,1, 4, 5);

        // sort(arr, 0, arr.length - 1);

        // tackleEnhance(0, arr.length - 1).accept(arr);

        //DataChecker.check(tackleEnhance(0, 10 - 1), 10);
        System.out.println(Arrays.toString(arr));
    }
}
