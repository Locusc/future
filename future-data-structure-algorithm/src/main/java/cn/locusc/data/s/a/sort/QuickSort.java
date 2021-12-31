package cn.locusc.data.s.a.sort;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * @author Jay
 * 快速排序(经典: 单轴快排)
 * 1.思路: 将最后一个元素作为中轴位, 左边界从左往右对比找比中轴位大,
 * 右边界从右往左对比找中轴位大小, 直接进行交换,
 * 分出两个区域(分别是比中轴位小和比中轴位大) 然后进行递归拆分排序
 *
 * 双轴快排
 * 选择两个轴位, 分成三个区域(小区, 中区, 大区)
 * 通过和轴位比较 确定出元素的区域(同时区域大小重新计算)
 * 两边区域不断吃掉中间区域
 *
 * 避免最坏时间(On²)
 * 1. 开始就判断数组是否是自然增长的
 * 2. 随机取一个数字放到最后 作为中轴位
 * 2021/12/18
 */
public class QuickSort {

    private static void sort(int[] arr, int leftBound, int rightBound) {
        // 说明数组只有一个元素
        if(leftBound >= rightBound) return;

        int mid = partition(arr, leftBound, rightBound);

        sort(arr, leftBound, mid - 1);
        sort(arr, mid + 1, rightBound);
    }

    private static int partition(int[] arr, int leftBound, int rightBound) {
        // 中轴
        int pivot = arr[rightBound];
        int left = leftBound;
        int right = rightBound - 1;

        while (left <= right) {
            // 从左往右 找到第一个比中轴大的数
            // left < right 如果中轴位是最大数
            // 那么就找不到比中轴位大的数
            // 会一直left ++ 从而下标越界 10

            // 等于是因为有可能边界位可能是最大数字
            while (left <= right && arr[left] <= pivot) {
                left ++;
            };

            // 从右往左 找到第一个比中轴小的数
            // left < right 如果中轴位是最大数
            // 那么就找不到比中轴位小的数
            // 会一直right -- 从而下标越界 -1

            // 后面为大于而不是>= 是因为如果数组有相同的数字
            // 那么等于的数字会到右分区
            while (left <= right && arr[right] > pivot){
                right --;
            };

            //System.out.println(left + "----" + right);

            // 如果小数的位置小于大数 那么交换位置
            if(left < right) {
                int temp = arr[left];
                arr[left] = arr[right];
                arr[right] = temp;
            }

            //System.out.println("arr: " + Arrays.toString(arr));

        }

        //System.out.println("final: " + left + "----" + right);
        // 因为最后left会大于right来终止循环
        // 所以这里的left已经指到了右分区的第一个数(如果边界位已经是最大, 那么就指到边界位上)
        // && pivot < arr[left]  判断在两个数时 left = right 会交换
        // left != rightBound 左边界和右边界重合 也就是轴位已经是最大数字 没比较进行交换
        if(left != rightBound) {
            int temp = arr[left];
            arr[left] = arr[rightBound];
            arr[rightBound] = temp;
        }

        return left;
    }

    // 单轴快排
    private static Consumer<int []> tackle(int leftBound, int rightBound) {
        return arr -> sort(arr, leftBound, rightBound);
    }

    public static void main(String[] args) {

        int[] arr = {7, 3, 2, 6, 8, 1, 9, 5, 4, 10};
        //int[] arr = {4, 6};
        //int[] arr = {1,4,7,8,3,6,9};

        //tackle(0, arr.length - 1).accept(arr);

        // tackleEnhance(0, arr.length - 1).accept(arr);

        DataChecker.check(tackle(0, 100 - 1), 100, 5, true);
        System.out.println(Arrays.toString(arr));
    }

}
