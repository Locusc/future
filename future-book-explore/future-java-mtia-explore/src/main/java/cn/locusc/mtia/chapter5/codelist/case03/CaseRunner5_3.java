package cn.locusc.mtia.chapter5.codelist.case03;

import cn.locusc.mtia.chapter4.codelist.case02.CaseRunner4_2;

public class CaseRunner5_3 {

    public static void main(String[] args) throws Exception {
        System.setProperty("x.stat.task",
                "cn.locusc.mtia.chapter5.codelist.case03.StatTask");
        CaseRunner4_2.main(args);
    }

}
