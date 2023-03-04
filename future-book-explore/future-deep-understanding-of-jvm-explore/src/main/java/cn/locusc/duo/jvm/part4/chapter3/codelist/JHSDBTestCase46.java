package cn.locusc.duo.jvm.part4.chapter3.codelist;

/**
 * @author Jay
 * staticObj, instanceObj, localObj 存放位置
 * 64操作系统需要禁止压缩指针, 有可能在使用jhsdb时无法对对象进行寻址操作
 * -Xmx10m -XX:+UseSerialGC -XX:-UseCompressedOops
 * java -cp your_javahome_path/lib/sa-jdi.jar sun.jvm.hotspot.HSDB
 * 2023/1/9
 */
public class JHSDBTestCase46 {

    static class Test {
        // 方法区 -> 元空间
        static ObjectHolder staticObj = new ObjectHolder();
        // 堆
        ObjectHolder instanceObj = new ObjectHolder();

        void foo() {
            // 局部变量表
            ObjectHolder objectHolder = new ObjectHolder();

            // 这里设置一个断点
            System.out.println("done");
        }
    }

    private static class ObjectHolder {}

    public static void main(String[] args) {
        Test test = new JHSDBTestCase46.Test();
        test.foo();
    }

}
