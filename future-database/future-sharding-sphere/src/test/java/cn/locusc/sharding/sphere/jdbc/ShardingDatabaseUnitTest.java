package cn.locusc.sharding.sphere.jdbc;

import cn.locusc.sharding.sphere.ShardingSphereApplication;
import cn.locusc.sharding.sphere.jdbc.entity.BOrder;
import cn.locusc.sharding.sphere.jdbc.entity.City;
import cn.locusc.sharding.sphere.jdbc.entity.Position;
import cn.locusc.sharding.sphere.jdbc.entity.PositionDetail;
import cn.locusc.sharding.sphere.jdbc.repository.BOrderRepository;
import cn.locusc.sharding.sphere.jdbc.repository.CityRepository;
import cn.locusc.sharding.sphere.jdbc.repository.PositionDetailRepository;
import cn.locusc.sharding.sphere.jdbc.repository.PositionRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Random;
import java.util.stream.Stream;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShardingSphereApplication.class)
public class ShardingDatabaseUnitTest {

    @Resource
    private PositionRepository positionRepository;

    @Resource
    private PositionDetailRepository positionDetailRepository;

    @Resource
    private CityRepository cityRepository;

    @Resource
    private BOrderRepository bOrderRepository;

    @Test
    public void singleAdd(){
        Stream.iterate(0, i -> i + 1).limit(20).forEach(f -> {
            Position position = new Position();
            // position.setId(i);
            position.setName("sharding" + f);
            position.setSalary("1000000");
            position.setCity("hangzhou");
            positionRepository.save(position);
        });
    }

    @Test
    public void add(){
        Stream.iterate(0, i -> i + 1).limit(20).forEach(f -> {
            Position position = new Position();
            position.setName("sharding" + f);
            position.setSalary("1000000");
            position.setCity("hangzhou");
            positionRepository.save(position);

            PositionDetail positionDetail = new PositionDetail();
            positionDetail.setPid(position.getId());
            positionDetail.setDescription("this is a message " + f);
            positionDetailRepository.save(positionDetail);
        });
    }

    @Test
    public void findPositionsById(){
        Position positionsById = positionRepository.findPositionsById(470186138993164289L);
        log.info(positionsById.toString());
    }

    @Test
    public void broadCastFromCity(){
        City city = new City();
        city.setName("hangzhou");
        city.setProvince("zhejiang");
        cityRepository.save(city);
    }

    @Test
    @Repeat(100)
    public void addOrder(){
        BOrder order = new BOrder();
        order.setIsDel(Boolean.FALSE);
        order.setCompanyId(new Random().nextInt(10));
        order.setPositionId(20221116);
        order.setUserId(20221116);
        order.setPublishUserId(20221116);
        order.setResumeType(1);
        order.setStatus("AUTO");
        order.setCreateTime(new Date());
        order.setOperateTime(new Date());
        order.setWorkYear("3");
        order.setName("wrench");
        order.setPositionName("BIG DATA");
        order.setResumeId(20221116);
        bOrderRepository.save(order);
    }

}
