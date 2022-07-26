package cn.locusc.mtia.chapter2.codelist;

import cn.locusc.mtia.utils.stf.*;

/**
 * @author Jay
 *
 * JIT编译器指令重排序
 *
 * 2022/5/30
 */
@ConcurrencyTest(iterations = 200000)
public class JITReorderingDemo210 {

    public static void main(String[] args) throws IllegalAccessException, InstantiationException {
        TestRunner.runTest(JITReorderingDemo210.class);
    }

    private int externalData = 1;
    private Helper helper;

    @Actor
    public void createHelper() {
        // 伪代码步骤表示
        // 1. 分配Helper实例所需的内存空间, 并获得一个指向该空间的引用
        // objRef = allcocate(Helper.class)
        // 2. 调用Helper类的构造器初始化objRef引用指向的Helper实例
        // inovkeConstructor(objRef)
        // 3. 将Helper实例引用objRef赋值给实例变量helper
        // helper = objRef
        helper = new Helper(externalData);
    }

    @Observer({
            @Expect(desc = "Helper is null", expected = -1),
            @Expect(desc = "Helper is not null,but it is not initialized",
                    expected = 0),
            @Expect(desc = "Only 1 field of Helper instance was initialized",
                    expected = 1),
            @Expect(desc = "Only 2 fields of Helper instance were initialized",
                    expected = 2),
            @Expect(desc = "Only 3 fields of Helper instance were initialized",
                    expected = 3),
            @Expect(desc = "Helper instance was fully initialized", expected = 4) })
    public int consume() {
        int sum = 0;

        /*
         * 由于我们未对共享变量helper进行任何处理（比如采用volatile关键字修饰该变量），
         * 因此，这里可能存在可见性问题，即当前线程读取到的变量值可能为null。
         */
        final Helper observedHelper = helper;
        if (null == observedHelper) {
            sum = -1;
        } else {
            sum = observedHelper.payloadA + observedHelper.payloadB
                    + observedHelper.payloadC + observedHelper.payloadD;
        }

        return sum;
    }

    static class Helper {
        int payloadA;
        int payloadB;
        int payloadC;
        int payloadD;

        public Helper(int externalData) {
            this.payloadA = externalData;
            this.payloadB = externalData;
            this.payloadC = externalData;
            this.payloadD = externalData;
        }

        @Override
        public String toString() {
            return "Helper [" + payloadA + ", " + payloadB + ", " + payloadC + ", "
                    + payloadD + "]";
        }

    }
}
