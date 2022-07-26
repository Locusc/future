package cn.locusc.mtia.chapter2.codelist;

/**
 * @author Jay
 *
 * 猜测执行Speculation
 * (78页 处理器可能会先计算出sum结果, 放在ROB中, 然后判断if条件)
 * true就写入高速缓存, false则抛弃ROB中的数据
 * 从底层的角度来说, 禁止重排序是通过调用处理器提供相应的指令（内存屏障）来实现的
 * 2022/5/30
 */
public class SpeculativeLoadExample211 {

    private boolean ready = false;

    // private volatile boolean ready = false;

    private int[] data = new int[]{1, 2, 3, 4, 5, 6, 7, 8};

    public void writer() {
        int[] newData = new int[]{1, 2, 3, 4, 5, 6, 7, 8};
        for (int i = 0; i < newData.length; i++) {// 语句①（for循环语句）
            // 此处包含读内存的操作
            newData[i] = newData[i] - i;
        }
        data = newData;
        // 此处包含写内存的操作
        ready = true;// 语句②
    }

    public int reader() {
        int sum = 0;
        int[] snapshot;
        if (ready) {// 语句③（if语句）
            snapshot = data;
            for (int i = 0; i < snapshot.length; i++) {// 语句④（for循环语句）
                sum += snapshot[i];// 语句⑤
            }

        }
        return sum;
    }
}
