package cn.locusc.java8action.domain;

import lombok.Data;

@Data
public class Accumulator {

    public long total = 0;
    public void add(long value) { total += value; }

}
