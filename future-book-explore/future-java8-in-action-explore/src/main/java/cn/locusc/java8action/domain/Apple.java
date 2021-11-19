package cn.locusc.java8action.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
public class Apple extends Fruit {

    private String color;
    private Integer weight;
    private String country;

    public Apple(Integer weight) {
        this.weight = weight;
    }

    public Apple(String color, Integer weight) {
        this.weight = weight;
        this.color = color;
    }

    public Apple(String color, Integer weight, String name) {
        super();
        this.weight = weight;
        this.color = color;
    }


    public static List<Apple> getApples() {
        String [] color = {"red", "green", "blue", "purple", "yellow", "black"};
        List<Apple> inventory = new ArrayList<>();
        Stream.iterate(0, i -> i + 1).limit(5).forEach(i -> {
            Apple apple = new Apple();
            apple.setColor(color[i]);
            apple.setWeight(i * 80);
            apple.setCountry(color[i] + "china");
            inventory.add(apple);
        });
        return inventory;
    }

}
