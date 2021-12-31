package cn.locusc.data.s.a.sort;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * @author Jay
 * 复习使用
 * 2021/12/13
 */
public class SortPractice {

    /**
     * 插入1
     * @return java.util.function.Consumer<int[]>
     */
    public static Consumer<int[]> insertion1() {
        return arr -> {
            int i,j;
            int target;
            for (j = 1; j < arr.length; j++) {
                target = arr[j];
                i = j;
                while (i > 0 && target < arr[i - 1]) {
                    int temp = arr[i];
                    arr[i] = arr[i - 1];
                    arr[i - 1] = temp;
                    i--;
                }
            }
        };
    }

    /**
     * 插入2
     * 12.22 插入排序内层 遍历的是已经排好序的列表
     * @return java.util.function.Consumer<int[]>
     */
    public static Consumer<int[]> insertion2() {
        return arr -> {
            for (int j = 1; j < arr.length; j++) {
                for (int i = j; i > 0; i--) {
                    if(arr[i] < arr[i - 1]) {
                        int temp = arr[i];
                        arr[i] = arr[i - 1];
                        arr[i - 1] = temp;
                    }
                }
            }
        };
    }

    /**
     * 冒泡
     * @return java.util.function.Consumer<int[]>
     */
    public static Consumer<int[]> bubbles() {
        return arr -> {
            boolean flag = true;
            for (int j = arr.length - 1; j > 0; j--) {
                for (int i = 0; i < j; i++) {
                    if(arr[i] > arr[i + 1]) {
                        int temp = arr[i];
                        arr[i] = arr[i + 1];
                        arr[i + 1] = temp;

                        flag = false;
                    }
                }

                if(flag) {
                    break;
                }
            };
        };
    }

    /**
     * 选择
     * @return java.util.function.Consumer<int[]>
     */
    public static Consumer<int[]> selection() {
        return arr -> {
            for (int j = 0; j < arr.length - 1; j++) {
                int minPosition = j;

                for (int i = j; i < arr.length; i++) {
                    minPosition = arr[minPosition] < arr[i] ? minPosition :i;
                }

                int temp = arr[j];
                arr[j] = arr[minPosition];
                arr[minPosition] = temp;
            }
        };
    }

    /**
     * 归并
     * @return java.util.function.Consumer<int[]>
     */
    public static Consumer<int[]> merge(int left, int right) {
        return arr -> sort(arr, left, right);
    }

    private static void sort(int[] arr, int left, int right) {
        if(left == right) {
            return;
        }
        int middle = left + (right - left) / 2;
        sort(arr, left, middle);
        sort(arr, middle + 1, right);

        combine(arr, left, middle + 1, right);
    }

    private static void combine(int[] arr, int leftOut, int rightOut, int bound) {

        int middleOut = rightOut - 1;
        int[] temp = new int[bound - leftOut + 1];

        int left = leftOut;
        int right = rightOut;

        int k = 0;

        while (left <= middleOut && right <= bound) {
            if(arr[left] <= arr[right]) {
                temp[k] = arr[left];
                left++;
            } else {
                temp[k] = arr[right];
                right++;
            }
            k++;
        }

        while (left <= middleOut) temp[k++] = arr[left++];
        while (right <= bound) temp[k++] = arr[right++];

        for (int i = 0; i < temp.length; i++) {
            arr[i + leftOut] = temp[i];
        }
    }

    /**
     * 希尔 @return java.util.function.Consumer<int[]>
     */
    public static Consumer<int[]> shell() {
        return arr -> {
            int h = 0;
            while (h  < arr.length / 3) {
                h = 3 * h + 1;
            }
            for (int gap = h; gap > 0; gap = (gap - 1) / 3) {
                for (int i = gap; i < arr.length; i++) {
                    for (int j = i; j > gap - 1; j-=gap) {
                        if(arr[j] < arr[j - gap]) {
                            int temp = arr[j];
                            arr[j] = arr[j - gap];
                            arr[j - gap] = temp;
                        }
                    }
                }
            }
        };
    }


    public static void main(String[] args) {
        //int[] arr = IntStream.rangeClosed(1, 9).toArray();
        int[] arr = {9,3,1,4,6,8,7,5,2};
        int[] arrMerge = {1, 3, 6, 7, 4, 8, 9};
        Consumer<int[]> selection = selection();
        //selection.accept(arr);
        //DataChecker.check(selection, 10_000);

        Consumer<int[]> bubbles = bubbles();
        //bubbles.accept(arr);
        //DataChecker.check(bubbles, 10_000);

        Consumer<int[]> insertion = insertion1();
        //insertion.accept(arr);
        //DataChecker.check(insertion, 10_000);

        Consumer<int[]> shell = shell();
        //shellshell.accept(arr);
        //DataChecker.check(shell, 10_000);

        Consumer<int[]> merge = merge(0, arrMerge.length - 1);
        //merge.accept(arrMerge);
        DataChecker.check(merge(0, 100_000 - 1), 100_000, 1, true);
        //combine(arrMerge, 1, 4, 6);

        //MergeSort.merge(arrMerge, 1, 4, 6);
//        System.out.println(Arrays.toString(arr));
        System.out.println(Arrays.toString(arrMerge));

        //System.out.println(-2 >> 2);
    }
}
