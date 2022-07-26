package cn.locusc.mtia.chapter5.codelist.case01;

import cn.locusc.mtia.utils.Tools;

public class CaseRunner5_1 {

    final static AlarmAgent51 alarmAgent;

    static {
        alarmAgent = AlarmAgent51.getInstance();
        alarmAgent.init();
    }

    public static void main(String[] args) throws InterruptedException {
        alarmAgent.sendAlarm("Database offline!");
        Tools.randomPause(12000);
        alarmAgent.sendAlarm("XXX service unreachable!");
    }

}
