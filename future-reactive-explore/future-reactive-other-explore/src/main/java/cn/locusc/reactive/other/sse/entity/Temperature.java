package cn.locusc.reactive.other.sse.entity;

public class Temperature {

    private final double value;

    public Temperature(double temperature) {
        this.value = temperature;
    }

    public double getValue() {
        return value;
    }

}
