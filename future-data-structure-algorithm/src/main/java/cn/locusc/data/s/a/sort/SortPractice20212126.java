package cn.locusc.data.s.a.sort;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

public class SortPractice20212126 {

    public static Consumer<int[]> quickSort(int leftBound, int rightBound) {
        return arr -> sortQuick(arr, leftBound, rightBound);
    }

    private static void sortQuick(int[]arr, int leftBound, int rightBound) {
        if(leftBound >= rightBound) {
            return;
        }

        int middle = partition(arr, leftBound, rightBound);

        System.out.println(middle);

        sortQuick(arr, leftBound,middle - 1);
        sortQuick(arr, middle + 1, rightBound);
    }

    private static int partition(int[] arr, int leftBound, int rightBound) {
        int axis = arr[rightBound];
        int left = leftBound;
        int right = rightBound - 1;

        while (left <= right) {
            // 找最大
            while (left <= right && arr[left] <= axis) left++;
            // 找最小
            while (left <= right && arr[right] > axis) right--;

            // 最大值的位置小于最小值
            if (left < right) {
                int temp = arr[left];
                arr[left] = arr[right];
                arr[right] = temp;
            }
        }

        if(left != rightBound) {
            int temp = arr[left];
            arr[left] = arr[rightBound];
            arr[rightBound] = temp;
        }
        return left;
    }

    public static Function<int[], int[]> countSort(int[] result) {
        return arr -> {
            int max = 0;
            int min = 0;
            for (int num : arr) {
                max = Math.max(max, num);
                min = Math.min(min, num);
            }

            int[] count = new int[max - min + 1];

            for (int i = 0; i < arr.length; i++) {
                count[arr[i]]++;
            }
            System.out.println(Arrays.toString(count));

            for (int i = 1; i < arr.length; i++) {
                count[i] = count[i] + count[i-1];
            }

            System.out.println(Arrays.toString(count));

            for (int i = arr.length - 1; i >= 0; i--) {
                result[--count[arr[i]]] = arr[i];
            }

            return result;
        };
    }

    public static Function<int[], int[]> radixSort(int[] result) {
        return arr -> {

            int[] count = new int[10];

            for (int i = 0; i < 3; i++) {
                int d = (int) Math.pow(10, i);
                for (int j = 0; j < arr.length; j++) {
                    int num = arr[j] / d % 10;
                    count[num]++;
                }

                for (int j = 1; j < arr.length; j++) {
                    count[j] = count[j] + count[j - 1];
                }

                for (int j = arr.length - 1; j >= 0; j--) {
                    int num = arr[j] / d % 10;
                    result[--count[num]] = arr[j];
                }

                System.arraycopy(result, 0, arr, 0, arr.length);
                Arrays.fill(count, 0);
            }

            return result;
        };
    }

    public static Consumer<int[]> selectSort() {
        return arr -> {
            for (int i = 0; i < arr.length; i++) {
                int min = i;

                // 第一次min=i=0 所以要从i+1开始对比
                for (int j = i + 1; j < arr.length; j++) {
                    min = arr[min] < arr[j] ? min : j;
                }

                int temp = arr[min];
                arr[min] = arr[i];
                arr[i] = temp;
            }
        };
    }

    public static Consumer<int[]> bubblesSort() {
        return arr -> {
            for (int j = arr.length - 1; j > 0; j--) {
                for (int i = 0; i < j; i++) {
                    if(arr[i] > arr[i+1]) {
                        int temp = arr[i];
                        arr[i] = arr[i+1];
                        arr[i+1] = temp;
                    }
                }
            }
        };
    }

    public static Consumer<int[]> insertionSort() {
        return arr -> {
            for (int i = 1; i < arr.length; i++) {
                for (int j = i; j > 0; j--) {
                    if(arr[j] < arr[j-1]) {
                        int temp = arr[j];
                        arr[j] = arr[j-1];
                        arr[j-1] = temp;
                    }
                }
            }
        };
    }

    public static Consumer<int[]> shellSort() {
        return arr -> {
//            for (int gap = arr.length >> 1; gap > 0 ; gap = gap / 2) {
//
//            }

            int h = 0;
            while (h < arr.length / 3) {
                h = h * 3 + 1;
            }

            for (int gap = h; gap > 0; gap = (gap -1) / 3) {
                for (int j = gap; j < arr.length; j++) {
                    for (int i = j; i > gap - 1; i-=gap) {
                        if(arr[i] < arr[i-gap]) {
                            int temp = arr[i];
                            arr[i] = arr[i-gap];
                            arr[i-gap] = temp;
                        }
                    }
                }
            }
        };
    }

    private static void sort(int[] arr, int left, int right) {
        if(left == right) {
            return;
        }

        int middle = left + (right - left) / 2;

        sort(arr, left, middle);
        sort(arr, middle + 1, right);

        merge(arr, left, middle + 1, right);
    }

    // 前提是子数组已经排好顺序
    private static void merge(int[] arr, int left, int right, int bound) {
        // 存放新元素的空间
        int [] temp = new int[bound - left + 1];
        // 中间数 因为传入的right是middle + 1 所以这里要减去
        int middle = right - 1;
        // 左起始位
        int l = left;
        // 右起始位
        int r = right;
        // 当前使用到temp的位置
        int k = 0;

        // 判断l<=中间 r<=边界位
        while (l <= middle && r <= bound){
            // 如果左起始的数字小于等于右起始的数字
            if(arr[l] <= arr[r]) {
                // 存放左起始位当前值
                temp[k] = arr[l];
                l++;
            } else {
                // 存放右起始位当前值
                temp[k] = arr[r];
                r++;
            }
            k++;
        }

        // 当两边有没有使用完的数组 全部复制下来
        while (l <= middle) temp[k++] = arr[l++];
        while (r <= bound) temp[k++] = arr[r++];

        for (int i = 0; i < temp.length; i++) {
            arr[i + left] = temp[i];
        }
    }


    private static Consumer<int []> mergeSort(int left, int right) {
        return arr -> sort(arr, left, right);
    }



    public static void main(String[] args) {
        int[] arr = {7, 3, 2, 6, 8, 1, 9, 5, 4, 0};
        //int[] arrRadix = {421,240,115,532,305,430,124};
        int[] arrMerge = {1, 3, 6, 7, 8, 2, 4, 5, 9, 10};

        //DataChecker.check(merge(0, 100_000 - 1), 100_000, 1);
        //DataChecker.check(quickSort(0, 10-1), 10, 1, true);

        //quickSort(0, arr.length - 1).accept(arr);

        //int[] apply = countSort(new int[arr.length]).apply(arr);
        //System.out.println(Arrays.toString(apply));

        //System.out.println(Arrays.toString(arr));

        //int[] apply = radixSort(new int[arrRadix.length]).apply(arrRadix);

        //selectSort().accept(arr);
        //DataChecker.check(selectSort(), 100, 1, true);

        //bubblesSort().accept(arr);
        //insertionSort().accept(arr);
        //shellSort().accept(arr);

        mergeSort(0, arr.length - 1).accept(arrMerge);

       //DataChecker.check(mergeSort(0, 100 - 1), 100, 2, true);
        System.out.println(Arrays.toString(arrMerge));
    }
}
