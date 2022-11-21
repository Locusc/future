package cn.locusc.sharding.sphere.jdbc;

import cn.locusc.sharding.sphere.ShardingSphereApplication;
import cn.locusc.sharding.sphere.jdbc.entity.City;
import cn.locusc.sharding.sphere.jdbc.repository.CityRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShardingSphereApplication.class)
public class ShardingMasterSlaveUnitTest {

    @Resource
    private CityRepository cityRepository;

    @Test
    public void add(){
        City city = new City();
        city.setName("hangzhou");
        city.setProvince("hangzhou");
        cityRepository.save(city);
    }

    @Test
    public void list(){
        List<City> list = cityRepository.findAll();
        list.forEach(f -> log.info(f.toString()));
    }

}
