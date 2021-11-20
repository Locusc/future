package cn.locusc.java8action.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Jay
 * 盘子
 * 2021/11/19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dish {

    public enum Type {
        MEAT,
        OTHER,
        FISH
    }

    private String name;
    private boolean vegetarian;
    private Integer calories;
    private Type type;

    public static List<Dish> getDish() {
        String [] name = {"Potato", "BambooShoots", "Tomato", "Cucumber", "Vegetable", "Pepper"};
        List<Dish> inventory = new ArrayList<>();
        Stream.iterate(0, i -> i + 1).limit(5).forEach(i -> {
            Dish dish = new Dish();
            dish.setCalories(i * 80);
            dish.setName(name[i]);
            inventory.add(dish);
        });
        return inventory;
    }

    public static List<Dish> getOfficial() {
        return Arrays.asList(
                new Dish("pork", false, 800, Dish.Type.MEAT),
                new Dish("beef", false, 700, Dish.Type.MEAT),
                new Dish("chicken", false, 400, Dish.Type.MEAT),
                new Dish("french fries", true, 530, Dish.Type.OTHER),
                new Dish("rice", true, 350, Dish.Type.OTHER),
                new Dish("season fruit", true, 120, Dish.Type.OTHER),
                new Dish("pizza", true, 550, Dish.Type.OTHER),
                new Dish("prawns", false, 300, Dish.Type.FISH),
                new Dish("salmon", false, 450, Dish.Type.FISH) );
    }

}
