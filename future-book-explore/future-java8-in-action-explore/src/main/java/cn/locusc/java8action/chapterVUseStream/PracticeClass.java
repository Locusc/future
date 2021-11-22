package cn.locusc.java8action.chapterVUseStream;

import cn.locusc.java8action.domain.Trader;
import cn.locusc.java8action.domain.Transaction54;
import com.alibaba.fastjson.JSON;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Jay
 * 5.5练习
 * 2021/11/21
 */
public class PracticeClass {

    public static void main(String[] args) {
        // (1) 找出2011年发生的所有交易，并按交易额排序（从低到高）
        List<Transaction54> transaction54s = Transaction54.transaction54s();
        List<Transaction54> collect = transaction54s
                .stream()
                .filter(value -> value.getYear() == 2011)
                .sorted(Comparator.comparing(Transaction54::getValue))
                .collect(Collectors.toList());
        System.out.println(JSON.toJSONString(collect));

        // (2) 交易员都在哪些不同的城市工作过？
        List<String> collect1 = transaction54s
                .stream()
                .map(Transaction54::getTrader)
                .map(Trader::getCity)
                .distinct()
                .collect(Collectors.toList());
        System.out.println(JSON.toJSONString(collect1));
        // 答案
        transaction54s
                .stream()
                .map(transaction -> transaction.getTrader().getCity())
                .distinct()
                .collect(Collectors.toList());

        Set<String> collect3 = transaction54s
                .stream()
                .map(transaction -> transaction.getTrader().getCity())
                .collect(Collectors.toSet());


        // (3) 查找所有来自于剑桥的交易员，并按姓名排序。
        List<Trader> cambridge = transaction54s
                .stream()
                .map(Transaction54::getTrader)
                .distinct()
                .filter(value -> value.getCity().equals("Cambridge"))
                .sorted(Comparator.comparing(Trader::getName))
                .collect(Collectors.toList());
        System.out.println(JSON.toJSONString(cambridge));
        // 答案
        List<Trader> traders = transaction54s.stream()
                .map(Transaction54::getTrader)
                .filter(trader -> trader.getCity().equals("Cambridge"))
                .distinct()
                .sorted(Comparator.comparing(Trader::getName))
                .collect(Collectors.toList());

        // (4) 返回所有交易员的姓名字符串，按字母顺序排序。
        List<String> collect2 = transaction54s
                .stream()
                .map(Transaction54::getTrader)
                .distinct()
                .map(Trader::getName)
                .sorted()
                .collect(Collectors.toList());
        System.out.println(JSON.toJSONString(collect2));
        // 答案
        String traderStr =
                transaction54s.stream()
                        .map(transaction -> transaction.getTrader().getName())
                        .distinct()
                        .sorted()
                        .collect(Collectors.joining());
                        // .reduce("", (n1, n2) -> n1 + n2);
        System.out.println("traderStr" + traderStr);

        // (5) 有没有交易员是在米兰工作的？
        boolean milan = transaction54s
                .stream()
                .map(Transaction54::getTrader)
                .distinct()
                .anyMatch(value -> value.getCity().equals("Milan"));
        System.out.println(milan);
        // 答案
        boolean milanBased = transaction54s.stream()
                        .anyMatch(transaction -> transaction.getTrader()
                                .getCity()
                                .equals("Milan"));

        // (6) 打印生活在剑桥的交易员的所有交易额。
        Optional<Integer> cambridge1 = transaction54s
                .stream()
                .filter(value -> value.getTrader().getCity().equals("Cambridge"))
                .map(Transaction54::getValue)
                .reduce(Integer::sum);
        System.out.println(cambridge1.get());
        // 答案
        transaction54s.stream()
                .filter(t -> "Cambridge".equals(t.getTrader().getCity()))
                .map(Transaction54::getValue)
                .forEach(System.out::println);

        // (7) 所有交易中，最高的交易额是多少？
        Optional<Integer> reduce = transaction54s
                .stream()
                .map(Transaction54::getValue)
                .reduce(Integer::max);
        System.out.println(reduce.get());

        // (8) 找到交易额最小的交易。
        Optional<Transaction54> reduce1 = transaction54s
                .stream()
                .reduce((a, b) -> a.getValue() < b.getValue() ? a : b);
        System.out.println(reduce1.get());
    }

}
