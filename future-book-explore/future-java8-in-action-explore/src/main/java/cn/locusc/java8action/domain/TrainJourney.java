package cn.locusc.java8action.domain;

import lombok.ToString;

@ToString
public class TrainJourney {

    public int price;

    public TrainJourney onward;

    public TrainJourney(int p, TrainJourney t) {
        price = p;
        onward = t;
    }

}
