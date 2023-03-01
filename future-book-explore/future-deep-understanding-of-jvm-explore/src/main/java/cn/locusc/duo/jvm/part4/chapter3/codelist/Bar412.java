package cn.locusc.duo.jvm.part4.chapter3.codelist;

/**
 * @author Jay
 * hsdis测试程序(代码如果很快结束, 即时编译器不能被jwatch捕获, 其实是需要设置-Xcomp,
 * 参数-Xcomp是让虚拟机以编译模式执行代码，这样不需要执行足够次数来预热就能触发即时编译)
 * -XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly -Xcomp -XX:CompileCommand=dontinline,*Bar412.sum -XX:CompileCommand=compileonly,*Bar412
 * -XX:+UnlockDiagnosticVMOptions -XX:+TraceClassLoading -XX:+LogCompilation -XX:LogFile=C:\Users\Jay\Desktop\jitwatch -XX:+PrintAssembly
 * 2023/2/11
 */
public class Bar412 {

    int a = 1;

    static int b = 2;

    public int sum(int c) {
        return a + b - c;
    }

    private Bar412() {
        int sum = 0;
        for (int i = 0; i < 100_00_00; i++) {
            sum = sum(sum);
        }
        System.out.println("SUM:" + sum);
    }

    public static void main(String[] args) {
        new Bar412();
    }

}