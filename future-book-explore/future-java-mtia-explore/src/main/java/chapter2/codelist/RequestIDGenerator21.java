package chapter2.codelist;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RequestIDGenerator21 implements CircularSeqGenerator21 {

    /**
     * 保存该类的唯一实例
     */
    private final static RequestIDGenerator21 INSTANCE = new RequestIDGenerator21();

    private final static short SEQ_UPPER_LIMIT = 999;

    private short sequence = -1;

    // 私有构造器
    private RequestIDGenerator21() {
        // 什么也不做
    }

    @Override
    public short nextSequence() {
        if (sequence >= SEQ_UPPER_LIMIT) {
            sequence = 0;
        } else {
            sequence++;
        }
        return sequence;
    }


    /**
     * 生成一个新的Request ID
     */
    public String nextID() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        String timestamp = sdf.format(new Date());
        DecimalFormat df = new DecimalFormat("000");

        // 生成请求序列号
        short sequenceNo = nextSequence();

        return "0049" + timestamp + df.format(sequenceNo);
    }

    /**
     * 返回该类的唯一实例
     */
    public static RequestIDGenerator21 getInstance() {
        return INSTANCE;
    }
}
