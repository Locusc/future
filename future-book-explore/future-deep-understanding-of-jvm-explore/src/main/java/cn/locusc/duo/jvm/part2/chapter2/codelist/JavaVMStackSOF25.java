package cn.locusc.duo.jvm.part2.chapter2.codelist;

/**
 * @author Jay
 * VM: JDK 1.0.2, Sun Classic VM
 *
 * 定义了大量的本地变量, 增大此方法帧中本地变量表的长度
 *
 * 我们继续验证第二种情况，这次代码就显得有些“丑陋”了，为了多占局部变量表空间，笔者不得
 * 不定义一长串变量，具体如代码清单2-5所示。
 * 代码清单2-5 虚拟机栈和本地方法栈测试（作为第2点测试程序）
 *
 * 实验结果表明：无论是由于栈帧太大还是虚拟机栈容量太小，当新的栈帧内存无法分配的时候，
 * HotSpot虚拟机抛出的都是StackOverflowError异常。可是如果在允许动态扩展栈容量大小的虚拟机
 * 上，相同代码则会导致不一样的情况。譬如远古时代的Classic虚拟机，这款虚拟机可以支持动态扩展
 * 栈内存的容量，在Windows上的JDK 1.0.2运行代码清单2-5的话（如果这时候要调整栈容量就应该改
 * 用-oss参数了），得到的结果是：
 *
 * stack length:3716
 * java.lang.OutOfMemoryError
 * at org.fenixsoft.oom. JavaVMStackSOF.leak(JavaVMStackSOF.java:27)
 * at org.fenixsoft.oom. JavaVMStackSOF.leak(JavaVMStackSOF.java:28)
 * at org.fenixsoft.oom. JavaVMStackSOF.leak(JavaVMStackSOF.java:28)
 * ……后续异常堆栈信息省略
 *
 * 可见相同的代码在Classic虚拟机中成功产生了OutOfMemoryError而不是StackOver-flowError异
 * 常。
 * 2022/7/29
 */
public class JavaVMStackSOF25 {

    private static int stackLength = 0;

    public static void test() {
        long unused1, unused2, unused3, unused4, unused5,
                unused6, unused7, unused8, unused9, unused10,
                unused11, unused12, unused13, unused14, unused15,
                unused16, unused17, unused18, unused19, unused20,
                unused21, unused22, unused23, unused24, unused25,
                unused26, unused27, unused28, unused29, unused30,
                unused31, unused32, unused33, unused34, unused35,
                unused36, unused37, unused38, unused39, unused40,
                unused41, unused42, unused43, unused44, unused45,
                unused46, unused47, unused48, unused49, unused50,
                unused51, unused52, unused53, unused54, unused55,
                unused56, unused57, unused58, unused59, unused60,
                unused61, unused62, unused63, unused64, unused65,
                unused66, unused67, unused68, unused69, unused70,
                unused71, unused72, unused73, unused74, unused75,
                unused76, unused77, unused78, unused79, unused80,
                unused81, unused82, unused83, unused84, unused85,
                unused86, unused87, unused88, unused89, unused90,
                unused91, unused92, unused93, unused94, unused95,
                unused96, unused97, unused98, unused99, unused100;

        stackLength ++;
        test();

        unused1 = unused2 = unused3 = unused4 = unused5 =
                unused6 = unused7 = unused8 = unused9 = unused10 =
                        unused11 = unused12 = unused13 = unused14 = unused15 =
                                unused16 = unused17 = unused18 = unused19 = unused20 =
                                        unused21 = unused22 = unused23 = unused24 = unused25 =
                                                unused26 = unused27 = unused28 = unused29 = unused30 =
                                                        unused31 = unused32 = unused33 = unused34 = unused35 =
                                                                unused36 = unused37 = unused38 = unused39 = unused40 =
                                                                        unused41 = unused42 = unused43 = unused44 = unused45 =
                                                                                unused46 = unused47 = unused48 = unused49 = unused50 =
                                                                                        unused51 = unused52 = unused53 = unused54 = unused55 =
                                                                                                unused56 = unused57 = unused58 = unused59 = unused60 =
                                                                                                        unused61 = unused62 = unused63 = unused64 = unused65 =
                                                                                                                unused66 = unused67 = unused68 = unused69 = unused70 =
                                                                                                                        unused71 = unused72 = unused73 = unused74 = unused75 =
                                                                                                                                unused76 = unused77 = unused78 = unused79 = unused80 =
                                                                                                                                        unused81 = unused82 = unused83 = unused84 = unused85 =
                                                                                                                                                unused86 = unused87 = unused88 = unused89 = unused90 =
                                                                                                                                                        unused91 = unused92 = unused93 = unused94 = unused95 =
                                                                                                                                                                unused96 = unused97 = unused98 = unused99 = unused100 = 0;
    }

    public static void main(String[] args) {
        try {
            test();
        }catch (Error e){
            System.out.println("stack length:" + stackLength);
            throw e;
        }
    }

}
