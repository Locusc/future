package cn.locusc.java8action.domain;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Random;

public class Discount {

    private static final DecimalFormat formatter = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.US));

    public enum Code {
        NONE(0), SILVER(5), GOLD(10), PLATINUM(15), DIAMOND(20);
        private final int percentage;
        Code(int percentage) {
            this.percentage = percentage;
        }
    }

    public static double format(double number) {
        synchronized (formatter) {
            return new Double(formatter.format(number));
        }
    }

    // Discount类的具体实现这里暂且不表示，参见代码清单11-14
    private static double apply(double price, Code code) {
        Shop.delay();
        return format(price * (100 - code.percentage) / 100);
    }

    // Discount服务还提供了一个applyDiscount方法，它接收一个Quote对象，返回一个字符
    // 串，表示生成该Quote的shop中的折扣价格
    public static String applyDiscount(Quote quote) {
        return quote.getShopName() + " price is " +
                Discount.apply(quote.getPrice(),
                        quote.getDiscountCode());
    }
}
