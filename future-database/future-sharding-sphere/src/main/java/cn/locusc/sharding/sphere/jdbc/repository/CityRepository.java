package cn.locusc.sharding.sphere.jdbc.repository;

import cn.locusc.sharding.sphere.jdbc.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> { }
