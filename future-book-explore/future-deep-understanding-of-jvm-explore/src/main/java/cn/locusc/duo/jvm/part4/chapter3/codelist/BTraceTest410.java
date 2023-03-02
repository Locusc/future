package cn.locusc.duo.jvm.part4.chapter3.codelist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Jay
 * 产生两个1000以内的随机整数, 输出这两个数字相加的结果
 * 2023/2/11
 */
public class BTraceTest410 {

    public int add(int a, int b) {
        return a + b;
    }

    public static void main(String[] args) throws IOException {
        BTraceTest410 test = new BTraceTest410();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        for (int i = 0; i < 10; i++) {
            reader.readLine();
            int a = (int) Math.round(Math.random() * 1000);
            int b = (int) Math.round(Math.random() * 1000);
            System.out.println(test.add(a, b));
        }
    }

/* BTrace Script Template 4-11
import com.sun.btrace.annotations.*;
        import static com.sun.btrace.BTraceUtils.*;

@BTrace
public class TracingScript {
    @OnMethod(
            clazz="org.fenixsoft.monitoring.BTraceTest",
            method="add",
            location=@Location(Kind.RETURN)
    )

    public static void func(@Self org.fenixsoft.monitoring.BTraceTest instance,int a,int b,@Return int result) {
        println("调用堆栈:");
        jstack();
        println(strcat("方法参数A:",str(a)));
        println(strcat("方法参数B:",str(b)));
        println(strcat("方法结果:",str(result)));
    }
}
*/

}
