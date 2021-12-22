package cn.locusc.data.s.a.sort;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * @author Jay
 * 冒泡排序(将前一个数字和后一个数字作比较, 大于则交换位置再于后面一个比较,
 * 以此类推, 移动到最后一个位置上, 然后下一次外循环将剩下的数字作为新的数组再执行比较逻辑)
 * 想象成泡泡往上冒
 *
 * 稳定原因:
 * 依次比较 不存在跳跃
 * todo 优化时间复杂度为O(n) 是否有交换产生
 * 2021/12/12
 */
public class BubbleSort {

    public static Consumer<int[]> tackle() {
        return arr -> {
            for (int i = arr.length - 1; i > 0; i--) {
                for (int j = 0; j < i; j++) {
                    if(arr[j] > arr[j+1]) {
                        int temp = arr[j];
                        arr[j] = arr[j+1];
                        arr[j+1] = temp;
                    }
                }
            }
            System.out.println(Arrays.toString(arr));
        };
    }

    public static void main(String[] args) {
        int[] arr = {9,3,1,4,6,8,7,5,2};

        Consumer<int[]> tackle = tackle();
        tackle.accept(arr);

        DataChecker.check(tackle(), 500_000);
    }

}
