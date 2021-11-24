package cn.locusc.java8action.chapter7Parallel;

import java.util.concurrent.RecursiveTask;

/**
 * @author Jay
 * 用分支/合并框架执行并行求和
 * 继承 RecursiveTask 来创建可以用于分支/合并框架的任务
 * 2021/11/24
 */
public class ForkJoinSumCalculator extends RecursiveTask<Long> {

    // 要求和数组
    private final long[] numbers;

    // 子任务处理的数组的起始和终止位置
    private final int start;
    private final int end;

    // 不再将任务分解为子任务的数组大小 10000
    public static final long THRESHOLD = 2;

    // 公共构造函数用于创建子任务
    public ForkJoinSumCalculator(long[] numbers) {
        this(numbers, 0, numbers.length);
    }

    // 私有构造函数用于以递归方式为主任务创建子任务
    private ForkJoinSumCalculator(long[] numbers, int start, int end) {
        this.numbers = numbers;
        this.start = start;
        this.end = end;
    }

    // 覆盖RecursiveTask抽象方法
    @Override
    protected Long compute() {
        // 该任务负责求和的部分的大小
        int length = end - start;
        if(length <= THRESHOLD) {
            // 如果大小小于或等于阈值，顺序计算结果
            return computeSequentially();
        }
        // 创建一个子任务为数组的前一半求和
        ForkJoinSumCalculator leftTask =
                new ForkJoinSumCalculator(numbers, start, start + length / 2);
        // 利用另一个ForkJoinPool线程异步执行新创建的子任务
        leftTask.fork();
        // 创建一个任务为数据的后一半求和
        ForkJoinSumCalculator rightTask =
                new ForkJoinSumCalculator(numbers, start + length / 2, end);

        // 同步执行第二个子任务 有可能允许进一步递归划分
        // 在不满足length <= THRESHOLD方法时 会一直递归调用改方法
        // 满足length <= THRESHOLD方法时计算后的属性会一直返回给rightResult
        // 二叉树遍历回到它的根
        Long rightResult = rightTask.compute();

        // 读取第一个子任务的结果 如果尚未完成就等待
        Long leftResult = leftTask.join();
        // 该任务的结果是两个子任务结果的组合
        return leftResult + rightResult;
    }

    // 在子任务不再可分时计算结果的简单算法
    private long computeSequentially() {
        long sum = 0;
        for (int i = start; i < end; i++) {
            sum += numbers[i];
        }
        return sum;
    }

}
