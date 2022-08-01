package cn.locusc.duo.jvm.part2.chapter2.codelist;

/**
 * @author Jay
 * VM Args：-Xss128k
 * 2022/7/29
 */
public class JavaVMStackSOF24_2 {

    private int stackLength = 1;

    public void stackLeak() {
        stackLength++;
        stackLeak();
    }

    public static void main(String[] args) {
        JavaVMStackSOF24_2 oom = new JavaVMStackSOF24_2();
        try {
            oom.stackLeak();
        } catch (Throwable e) {
            System.out.println("stack length:" + oom.stackLength);
            throw e;
        }
    }

}
