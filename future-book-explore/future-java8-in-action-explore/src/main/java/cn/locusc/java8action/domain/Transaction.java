package cn.locusc.java8action.domain;

import lombok.Data;

@Data
public class Transaction {

    private int price;
    private Currency currency;

    public void getValue() {

    }

}
