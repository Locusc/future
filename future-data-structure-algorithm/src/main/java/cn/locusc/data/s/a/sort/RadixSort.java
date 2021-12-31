package cn.locusc.data.s.a.sort;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * @author Jay
 * 基数排序(从低位到高位或从高位到低位进行计数排序, (120, 320, 249))
 * 1.本质上是一种多关键字排序
 * 2.有低位优先和高位优先两种
 *  LSD(低位), MSD(高位, 递归, 分治思想)
 * 3.百度百科使用二位数组存放计数数组会造成空间复杂度提高
 * 2021/12/26
 */
public class RadixSort {

    /**
     * 稳定
     * todo 判断最大位数 以及补零
     * @param result 结果数组
     * @return java.util.function.Consumer<int[]>
     */
    private static Consumer<int []> tackle(int[] result) {
        return arr -> {
            int[] count = new int[10];

            for (int i = 0; i < 3; i++) {
                // 返回10的i幂次方
                int division = (int) Math.pow(10, i);
                for (int j = 0; j < arr.length; j++) {
                    int num = arr[j] / division % 10;

                    System.out.println(num + " ");

                    count[num]++;
                }
                System.out.println();
                System.out.println(Arrays.toString(count));

                // 累加数组 记录相同元素的最后一个元素的位置
                for (int m = 1; m < count.length; m++) {
                    count[m] = count[m] + count[m-1];
                }
                System.out.println(Arrays.toString(count));

                // 从后向前遍历需要排序的数组
                for (int n = arr.length - 1; n >= 0; n--) {
                    int num = arr[n] / division % 10;

                    // count[arr[i]]从累加数组, 获取相同元素的最后一个元素的位置
                    // --i是因为 比如0出现了三次
                    // 第一次最后的位置为3
                    // 赋值给result后 那么下个0出现的最后位置就应该--i1
                    result[--count[num]] = arr[n];
                }

                System.out.println("根据第" + i + "的排序结果 = " + Arrays.toString(result));


                System.arraycopy(result, 0, arr, 0, arr.length);
                // 填充数组 这里的目的是为了将计数数组初始化
                Arrays.fill(count, 0);
            }


        };
    }

    public static void main(String[] args) {

        int[] arr = {421,240,115,532,305,430,124};

        int[] result = new int[arr.length];

        //tackle(result).accept(arr);

        tackle(result).accept(arr);

        System.out.println(Arrays.toString(result));
    }

}
