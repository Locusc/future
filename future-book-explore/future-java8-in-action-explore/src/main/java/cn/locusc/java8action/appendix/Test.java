package cn.locusc.java8action.appendix;

import cn.locusc.java8action.domain.Dish;
import com.alibaba.fastjson.JSON;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;

public class Test {

    public static void main(String[] args) {
        //将StreamForker运用于实战
        Stream<Dish> menuStream = Dish.getOfficial().stream();
        StreamFork.Results results = new StreamFork<>(menuStream)
                .fork("shortMenu", s -> s.map(Dish::getName).collect(joining(", ")))
                .fork("totalCalories", s -> s.mapToInt(Dish::getCalories).sum())
                .fork("mostCaloricDish", s -> s.max(Comparator.comparing(Dish::getCalories)).get())
                .fork("dishesByType", s -> s.collect(groupingBy(Dish::getType)))
                .getResults();

        String shortMenu = results.get("shortMenu");
        int totalCalories = results.get("totalCalories");
        Dish mostCaloricDish = results.get("mostCaloricDish");
        Map<Dish.Type, List<Dish>> dishesByType = results.get("dishesByType");
        System.out.println("Short menu: " + shortMenu);
        System.out.println("Total calories: " + totalCalories);
        System.out.println("Most caloric dish: " + JSON.toJSONString(mostCaloricDish));
        System.out.println("Dishes by type: " + dishesByType);

        Dish.getOfficial().forEach(value -> {
            String name = value.getName();
        });
    }
}
