package cn.locusc.sharding.sphere.jdbc;

import cn.locusc.sharding.sphere.ShardingSphereApplication;
import cn.locusc.sharding.sphere.jdbc.entity.CUser;
import cn.locusc.sharding.sphere.jdbc.repository.CUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShardingSphereApplication.class)
public class ShardingEncryptorUnitTest {

    @Resource
    private CUserRepository userRepository;

    @Test
    @Repeat(2)
    public void add(){
        CUser user = new CUser();
        user.setName("tiger");
        user.setPwd("abc");
        userRepository.save(user);
    }

    @Test
    public void testFind(){
        List<CUser> list = userRepository.findByPwd("abc");
        list.forEach(f -> log.info(f.toString()));
    }

}
