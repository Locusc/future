package cn.locusc.data.s.a.sort;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Jay
 * 计数排序(非比较排序,创建一个新的数组(计数数组)用来计数, 计算元素的出现次数
 * 并且将下标当作元素的值, 最后遍历计数数组, 从而得出排序后的数组)
 * 1.适合数据量大但是数据范围小
 * 2.如何快速得知高考名次(腾讯面试)
 *
 * 问题:
 * 1.当元素不是从0开始
 * 2.当元素是离散的 类似0-10 但是中间没有3, 7, 9
 * 3.todo 计数数组的长度划分问题
 * 2021/12/26
 */
public class CountSort {

    /**
     * 不稳定
     * @param result 结果数组
     * @return java.util.function.Consumer<int[]>
     */
    private static Consumer<int []> tackle(int[] result) {
        return arr -> {
            // 计数数组
            int[] count = new int[10];
            for (int i = 0; i < arr.length; i++) {
                // 将元素的出现次数放入计数数组
                count[arr[i]]++;
            }

            System.out.println(Arrays.toString(count));

            // 遍历计数数组
            for (int i = 0, j = 0; i < count.length; i++) {
                // 判断当前次数是不是大于0
                // 大于0的话, result接收j++个i(此时i元素的值)
                while (count[i]-- > 0) result[j++] = i;
            }
        };
    }

    /**
     * 稳定
     * @param result 结果数组
     * @return java.util.function.Consumer<int[]>
     */
    private static Function<int [], int []> tackleEnhance(int[] result) {
        return arr -> {
            int max = Integer.MIN_VALUE;
            int min = Integer.MAX_VALUE;
            for (int num : arr) {
                max = Math.max(max, num);
                min = Math.min(min, num);
            }

            // 初始化计数数组count
            // 长度为最大值减最小值加1
            // 计数数组
            int[] count = new int[max-min+1];
            for (int i = 0; i < arr.length; i++) {
                // 将元素的出现次数放入计数数组
                count[arr[i]]++;
            }

            System.out.println(Arrays.toString(count));

            // 累加数组 记录相同元素的最后一个元素的位置
            for (int i = 1; i < count.length; i++) {
                count[i] = count[i] + count[i-1];
            }

            System.out.println(Arrays.toString(count));

            // 从后向前遍历需要排序的数组
            for (int i = arr.length - 1; i >= 0; i--) {
                // count[arr[i]]从累加数组, 获取相同元素的最后一个元素的位置

                // --i是因为 比如0出现了三次
                // 第一次最后的位置为3
                // 赋值给result后 那么下个0出现的最后位置就应该--i1
                result[--count[arr[i]]] = arr[i];
            }

            //System.arraycopy(arr, 0, result, 0, result.length);

            return result;
        };
    }

    public static void main(String[] args) {

        int[] arr = {2,4,2,3,7,1,1,0,0,5,6,9,8,5,7,4,0,9, 20};
        //int[] arr = {2,4,2,3,7,1,1,5,6,9,8,5,7,4,9};

        int[] result = new int[arr.length];

        //tackle(result).accept(arr);

        //tackleEnhance(result).accept(arr);

        DataChecker.check(tackleEnhance(new int[10]), 10, 1, 10, true);

        //System.out.println(Arrays.toString(result));
    }

}
