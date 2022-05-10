package cn.locusc.distributed.pk;

import org.junit.Test;
import redis.clients.jedis.Jedis;

public class PKFromRedisGenerator {

    @Test
    public void pKFromRedisGenerator() {
        Jedis jedis = new Jedis("192.168.0.46",6379);
        // <id,0>
        Long id = jedis.incr("id");
        System.out.println(id);
    }

}
