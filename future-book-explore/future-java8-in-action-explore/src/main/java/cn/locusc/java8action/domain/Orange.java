package cn.locusc.java8action.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Data
@NoArgsConstructor
public class Orange extends Fruit {

    private String color;
    private Integer weight;

    public Orange(Integer weight) {
        this.weight = weight;
    }

    public Orange(String color, Integer weight) {
        this.weight = weight;
        this.color = color;
    }


    public static List<Orange> getApples() {
        String [] color = {"red", "green", "blue", "purple", "yellow", "black"};
        List<Orange> inventory = new ArrayList<>();
        Stream.iterate(0, i -> i + 1).limit(5).forEach(i -> {
            Orange orange = new Orange();
            orange.setColor(color[i]);
            orange.setWeight(i * 80);

            inventory.add(orange);
        });
        return inventory;
    }

}
