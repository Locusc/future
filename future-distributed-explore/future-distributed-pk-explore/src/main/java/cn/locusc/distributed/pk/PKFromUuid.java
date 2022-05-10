package cn.locusc.distributed.pk;

import org.junit.Test;

import java.util.UUID;

public class PKFromUuid {

    @Test
    public void pKFromUuid() {
        UUID uuid = UUID.randomUUID();
        System.out.println(uuid);
    }

}
