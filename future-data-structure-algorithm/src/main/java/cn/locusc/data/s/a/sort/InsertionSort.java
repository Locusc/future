package cn.locusc.data.s.a.sort;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * @author Jay
 * 插入排序(通过构建有序序列,对于未排序数据,在已排序序列中从后向前扫描,找到相应的位置并插入)
 * todo 通过位运算符优化语句
 * 2021/12/12
 */
public class InsertionSort {

    public static Consumer<int []> tackle() {
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
            System.out.println(Arrays.toString(arr));
        };
    }

    /**
     * 用临时变量记录标记项, 去掉交换方法
     */
    public static Consumer<int[]> tackleEnhance() {
        return arr -> {
            int i, j;
            int target;

            for (i = 1; i < arr.length; i++) {
                j = i;
                target = arr[i];

                while (j > 0 && target < arr[j - 1]) {
                    arr[j] = arr[j - 1];
                    j--;
                }

                arr[j] = target;
            }
            System.out.println(Arrays.toString(arr));
        };
    }

    public static void main(String[] args) {
        int[] arr = {9,3,1,4,6,8,7,5,2};

        Consumer<int[]> tackleEnhance = tackleEnhance();
        tackleEnhance.accept(arr);

        Consumer<int[]> tackle = tackle();
        tackle.accept(arr);

        DataChecker.check(tackleEnhance(), 10000, 1, true);
    }

}
