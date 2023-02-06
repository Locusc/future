package cn.locusc.duo.jvm.part4.chapter3.codelist;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jay
 * 内存占位符对象, 一个OOMObject47大约占64KB
 * -Xms100m -Xmx100m -XX:+UseSerialGC
 * 2023/1/9
 */
class JConsoleTestCase47 {

    static class OOMObject {
        public byte[] placeHolder = new byte[64 * 1024];
    }

    public static void fillHeap(int num) throws InterruptedException {
        List<OOMObject> list = new ArrayList<OOMObject>();
        for (int i = 0; i < num; i++) {
            // 稍作延时, 令监视曲线的变化更加明显
            Thread.sleep(50);
            list.add(new OOMObject());
        }
        // 这里无法回收是因为gc过后, fillHeap方法并没有退出
        // 所以在局部变量表的引用类型仍然持有对list的引用所以老年代无法被gc回收
        // 只要将System.gc();移动到fillHeap(1000);后面就会被回收
        System.gc();
    }

    public static void main(String[] args) throws Exception {
        fillHeap(1000);
    }

}
