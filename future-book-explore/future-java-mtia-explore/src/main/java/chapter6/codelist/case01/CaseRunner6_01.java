package chapter6.codelist.case01;


import utils.Debug;

import java.text.DecimalFormat;

public class CaseRunner6_01 implements Runnable {

    public static void main(String[] args) {
        CaseRunner6_01 self = new CaseRunner6_01();
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            new Thread(self).start();
        }
    }

    @Override
    public void run() {
        // 生成强随机数验证码
        int verificationCode = ThreadSpecificSecureRandom68.INSTANCE.nextInt(999999);
        DecimalFormat df = new DecimalFormat("000000");
        String txtVerCode = df.format(verificationCode);
        sendSms(txtVerCode);
    }

    private void sendSms(String txtVerCode) {
        Debug.info("sending " + txtVerCode);
        // ...
    }

}
