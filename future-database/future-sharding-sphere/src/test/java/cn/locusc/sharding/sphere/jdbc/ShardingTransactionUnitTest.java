package cn.locusc.sharding.sphere.jdbc;

import cn.locusc.sharding.sphere.ShardingSphereApplication;
import cn.locusc.sharding.sphere.jdbc.entity.Position;
import cn.locusc.sharding.sphere.jdbc.entity.PositionDetail;
import cn.locusc.sharding.sphere.jdbc.repository.PositionDetailRepository;
import cn.locusc.sharding.sphere.jdbc.repository.PositionRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.transaction.core.TransactionType;
import org.apache.shardingsphere.transaction.core.TransactionTypeHolder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.transaction.Transactional;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShardingSphereApplication.class)
public class ShardingTransactionUnitTest {

    @Resource
    private PositionRepository positionRepository;

    @Resource
    private PositionDetailRepository positionDetailRepository;

    /**
     * 使用自定义的jta.properties需要在@SpringBootApplication
     * 排除springboot的默认实现配置JtaAutoConfiguration.class
     */
    @Test
    @Transactional
    // @ShardingTransactionType(TransactionType.XA)
    // @ShardingTransactionType(TransactionType.BASE)
    public void test() {
        TransactionTypeHolder.set(TransactionType.XA);
        // TransactionTypeHolder.set(TransactionType.BASE);
        for (int i=1;i<=20;i++) {
            Position position = new Position();
            position.setName("locusc" + i);
            position.setSalary("1000000");
            position.setCity("hangzhou");
            positionRepository.save(position);

            if(i == 3) {
                throw new RuntimeException("throw error");
            }

            PositionDetail positionDetail = new PositionDetail();
            positionDetail.setPid(position.getId());
            positionDetail.setDescription("this is a message " + i);
            positionDetailRepository.save(positionDetail);
        }
    }

}
