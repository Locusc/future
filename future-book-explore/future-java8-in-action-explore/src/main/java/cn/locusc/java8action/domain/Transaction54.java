package cn.locusc.java8action.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
public class Transaction54 {

    private final Trader trader;
    private final int year;
    private final int value;

    public static List<Transaction54> transaction54s() {
        Trader raoul = new Trader("Raoul", "Cambridge");
        Trader mario = new Trader("Mario","Milan");
        Trader alan = new Trader("Alan","Cambridge");
        Trader brian = new Trader("Brian","Cambridge");

        return Arrays.asList(
                new Transaction54(brian, 2011, 300),
                new Transaction54(raoul, 2012, 1000),
                new Transaction54(raoul, 2011, 400),
                new Transaction54(mario, 2012, 710),
                new Transaction54(mario, 2012, 700),
                new Transaction54(alan, 2012, 950)
        );

    }

}
