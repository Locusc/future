package cn.locusc.java8action.chapterII;

import cn.locusc.java8action.domain.Apple;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Jay
 * 通过行为参数化传递代码
 * 2021/11/16
 * 小结:
 *  行为参数化，就是一个方法接受多个不同的行为作为参数，并在内部使用它们，完成不
 * 同行为的能力。
 *  行为参数化可让代码更好地适应不断变化的要求，减轻未来的工作量。
 *  传递代码，就是将新行为作为参数传递给方法。但在Java 8之前这实现起来很啰嗦。为接
 * 口声明许多只用一次的实体类而造成的啰嗦代码，在Java 8之前可以用匿名类来减少。
 *  Java API包含很多可以用不同行为进行参数化的方法，包括排序、线程和GUI处理。
 */
public class ActionClass {

    public static List<Apple> filterGreenApples(List<Apple> inventory) {
        List<Apple> result = new ArrayList<>();
        for(Apple apple: inventory){
            if( "green".equals(apple.getColor() )) {
                result.add(apple);
            }
        }
        return result;
    }

    public static List<Apple> filterApplesByColor(List<Apple> inventory,
                                                  String color) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple: inventory){
            if ( apple.getColor().equals(color) ) {
                result.add(apple);
            }
        }
        return result;
    }

    public static List<Apple> filterApplesByWeight(List<Apple> inventory,
                                                   int weight) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple: inventory){
            if ( apple.getWeight() > weight ){
                result.add(apple);
            }
        }
        return result;
    }

    public static List<Apple> filterApples(List<Apple> inventory, String color,
                                           int weight, boolean flag) {
        List<Apple> result = new ArrayList<Apple>();
        for (Apple apple: inventory){
            if ( (flag && apple.getColor().equals(color)) ||
                    (!flag && apple.getWeight() > weight) ){
                result.add(apple);
            }
        }
        return result;
    }

    public interface ApplePredicate {
        boolean test (Apple apple);
    }

    public static class AppleHeavyWeightPredicate implements ApplePredicate {
        public boolean test(Apple apple){
            return apple.getWeight() > 150;
        }
    }

    public class AppleGreenColorPredicate implements ApplePredicate {
        public boolean test(Apple apple){
            return "green".equals(apple.getColor());
        }
    }

    public static class AppleRedAndHeavyPredicate implements ApplePredicate {
        public boolean test(Apple apple){
            return "red".equals(apple.getColor())
                    && apple.getWeight() > 150;
        }
    }

    public static List<Apple> filterApples(List<Apple> inventory,
                                           ApplePredicate p) {
        List<Apple> result = new ArrayList<>();
        for (Apple apple : inventory) {
            if (p.test(apple)) {
                result.add(apple);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        List<Apple> inventory = Apple.getApples();
        filterApplesByColor(inventory, "green");
        filterApplesByColor(inventory, "red");
        filterApples(inventory, "green", 0, true);
        filterApples(inventory, "", 150, false);

        filterApples(inventory, new AppleHeavyWeightPredicate());


        // 用 Comparator 来排序
        inventory.sort(new Comparator<Apple>() {
            public int compare(Apple a1, Apple a2){
                return a1.getWeight().compareTo(a2.getWeight());
            }
        });

        // 用 Runnable 执行代码块
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("hello world");
            }
        });

        new Thread(() -> System.out.println("hello world"));

        // GUI 事件处理
        // Button button = new Button("Send");
        // button.setOnAction(new EventHandler<ActionEvent>() {
        //    public void handle(ActionEvent event) {
        //        label.setText("Sent!!");
        //    }
        // });

        // button.setOnAction((ActionEvent event) -> label.setText("Sent!!"));


    }

}
