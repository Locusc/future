package cn.locusc.java8action.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class Shop {

    private String name;

    public static void findShopPerformance(String product, Function<String, List<String>> func) {
        long start = System.nanoTime();
        System.out.println(func.apply(product));
        long duration = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Done in " + duration + " msecs");
    }

    public static Function<String, List<String>> findPricesParallel() {
        return name -> getShops().stream()
                .map(shop -> String.format("%s price is %.2f",
                        shop.getName(), shop.getPrice(name)))
                .parallel()
                .collect(Collectors.toList());
    }

    public static Function<String, List<String>> findPrices() {
        return name -> getShops().stream()
                .map(shop -> String.format("%s price is %.2f",
                        shop.getName(), shop.getPrice(name)))
                .collect(Collectors.toList());
    }

    public static List<Shop> getShops() {
        return Arrays.asList(new Shop("BestPrice"),
                new Shop("LetsSaveBig"),
                new Shop("MyFavoriteShop"),
                new Shop("BuyItAll"),
                new Shop("BuyItAll"),
                new Shop("BuyItAll"),
                new Shop("BuyItAll"),
                new Shop("BuyItAll"),
                new Shop("CostCo"));
    }

    public static void delay() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static double calculatePrice(String product) {
        // throw new RuntimeException("product not available");
        randomDelay();
        return new Random().nextDouble() * product.charAt(0) + product.charAt(1);
    }

    private static final Random random = new Random();

    private static void randomDelay() {
        int delay = 500 + random.nextInt(2000);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static double getPrice(String product) {
        return calculatePrice(product);
    }

    public String getPriceDiscount(String product) {
        double price = calculatePrice(product);
        Discount.Code code = Discount.Code.values()[
                new Random().nextInt(Discount.Code.values().length)];
        return String.format("%s:%.2f:%s", this.name, price, code);
    }

    public Future<Double> getPriceAsync(String product) {
        CompletableFuture<Double> futurePrice = new CompletableFuture<>();
        new Thread(() -> {
            double price;
            try {
                price = calculatePrice(product);
                // 如果价格计算正常结束，完成Future操作并设置商品价格
                futurePrice.complete(price);
            } catch (Exception ex) {
                // 否则就抛出导致失败的异常，完成这次Future操作
                futurePrice.completeExceptionally(ex);
            }
        }).start();
        return futurePrice;
    }

    public Future<Double> getPriceAsyncImproves(String product) {
        return CompletableFuture.supplyAsync(() -> calculatePrice(product));
    }

}
