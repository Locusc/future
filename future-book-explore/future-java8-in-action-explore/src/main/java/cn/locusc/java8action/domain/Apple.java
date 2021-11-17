package cn.locusc.java8action.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Data
public class Apple {

    private String color;
    private Integer weight;

    public static List<Apple> getApples() {
        String [] color = {"red", "green", "blue", "purple", "yellow", "black"};
        List<Apple> inventory = new ArrayList<>();
        Stream.iterate(0, i -> i + 1).limit(5).forEach(i -> {
            Apple apple = new Apple();
            apple.setColor(color[i]);
            apple.setWeight(i * 80);

            inventory.add(apple);
        });
        return inventory;
    }

}
