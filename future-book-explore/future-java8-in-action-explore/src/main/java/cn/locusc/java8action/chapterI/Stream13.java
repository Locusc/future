package cn.locusc.java8action.chapterI;

import cn.locusc.java8action.domain.Apple;
import cn.locusc.java8action.domain.Currency;
import cn.locusc.java8action.domain.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * 流
 */
public class Stream13 {

    public static void main(String[] args) {
        List<Transaction> transactions = new ArrayList<>();

        // 分组
        // old 中筛选金额较高的交易，然后按货币分组
        Map<Currency, List<Transaction>> transactionsByCurrencies =
                new HashMap<>();
        for (Transaction transaction : transactions) {
            if(transaction.getPrice() > 1000){
                Currency currency = transaction.getCurrency();
                List<Transaction> transactionsForCurrency =
                        transactionsByCurrencies.get(currency);
                if (transactionsForCurrency == null) {
                    transactionsForCurrency = new ArrayList<>();
                    transactionsByCurrencies.put(currency,
                            transactionsForCurrency);
                }
                transactionsForCurrency.add(transaction);
            }
        }

        // new
        Map<Currency, List<Transaction>> collect = transactions
                .stream()
                .filter((Transaction t) -> t.getPrice() > 1000)
                .collect(groupingBy(Transaction::getCurrency));

        // #####################################################
        // 串行和并行
        List<Apple> inventory = new ArrayList<>();
        // 串行
        List<Apple> collect1 = inventory.stream().filter((Apple a) -> a.getWeight() > 150)
                .collect(toList());
        // 并行
        List<Apple> collect2 = inventory.parallelStream().filter((Apple a) -> a.getWeight() > 150)
                .collect(toList());

    }



}
