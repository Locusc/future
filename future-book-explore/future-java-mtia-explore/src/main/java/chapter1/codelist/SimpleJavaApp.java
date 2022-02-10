package chapter1.codelist;

import java.util.Date;

/**
 * @author Jay
 * 代码清单1-2
 * 2022/2/9
 */
public class SimpleJavaApp {

    public static void main(String[] args) throws InterruptedException {
        for (;;) {
            System.out.println(new Date());
            Thread.sleep(1000);
        }
    }

}
