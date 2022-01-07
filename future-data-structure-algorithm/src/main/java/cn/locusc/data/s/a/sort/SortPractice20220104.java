package cn.locusc.data.s.a.sort;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * @author Jay
 * 排序回顾
 * 2022/1/4
 */
public class SortPractice20220104 {


    private static Consumer<int[]> mergeSort(int left, int right) {
        return arr -> sort(arr, left, right);
    }

    private static void sort(int []arr, int left, int right) {
        if(left == right) {
            return;
        }
        int middle = left + (right - left) /2;
        sort(arr, left, middle);
        sort(arr, middle + 1, right);

        merge(arr, left, middle + 1, right);
    }

    private static void merge(int []arr, int lefts, int rights, int bound) {
        int[] temp = new int[bound - lefts + 1];

        int middle = rights - 1;

        int left = lefts;
        int right = rights;

        int k = 0;

        while (left <= middle && right <= bound) {
            if(arr[left] <= arr[right]) {
                temp[k] = arr[left];
                left++;
            } else {
                temp[k] = arr[right];
                right++;
            }
            k++;
        }

        while (left <= middle) temp[k++] = arr[left++];
        while (right <= bound) temp[k++] = arr[right++];

        for (int i = 0; i < temp.length; i++) {
            arr[i + lefts] = temp[i];
        }

    }

    private static Consumer<int[]> shellSort() {
        return arr -> {
            for (int gap = arr.length >> 1; gap > 0; gap /= 2) {
                // int i = gap
                for (int i = gap; i < arr.length; i++) {
                    // j > gap - 1
                    for (int j = i; j > gap - 1; j-=gap) {
                        // arr[j] < arr[j-gap]
                        if(arr[j] < arr[j-gap]) {
                            int temp = arr[j];
                            arr[j] = arr[j-gap];
                            arr[j-gap] = temp;
                        }
                    }
                }
            }
        };
    }

    private static Consumer<int[]> insertionSort() {
        return arr -> {
            for (int j = 1; j < arr.length; j++) {
                for (int i = j; i > 0; i--) {
                    if(arr[i] < arr[i-1]) {
                        int temp = arr[i];
                        arr[i] = arr[i-1];
                        arr[i-1] = temp;
                    }
                }
            }
        };
    }

    private static Consumer<int[]> bubblesSort() {
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

    private static Consumer<int[]> selectSort() {
        return arr -> {
            for (int j = 0; j < arr.length; j++) {
                int min = j;
                for (int i = j + 1; i < arr.length; i++) {
                    min = arr[min] < arr[i] ? min : i;
                }
                int temp = arr[j];
                arr[j] = arr[min];
                arr[min] = temp;
            }
        };
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
        //DataChecker.check(bubblesSort(), 100, 1, true);

        //insertionSort().accept(arr);
        //DataChecker.check(selectSort(), 100, 1, true);

        //shellSort().accept(arr);
        //DataChecker.check(selectSort(), 100, 1, true);

        //mergeSort(0, arrMerge.length - 1).accept(arrMerge);
        DataChecker.check(mergeSort(0, 100 - 1), 100, 2, true);

        System.out.println(Arrays.toString(arrMerge));
    }

}
