package chapter4.codelist.case02;

import java.util.Map;

public interface StatProcessor {

    void process(String record);

    Map<Long, DelayItem> getResult();

}
