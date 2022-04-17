package cn.locusc.project.reactor.jpa.crud.repository;

import cn.locusc.project.reactor.jpa.crud.entity.LoanOrder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Jay
 * 自定义方法
 * 2022/3/20
 */
@Repository
public interface LoanOrderJpaRepository extends CrudRepository<LoanOrder, Integer> {

    /**
     * 查询指定范围 jpql
     * @return java.util.List<cn.hrfax.webflux.locusc.LoanOrder>
     */
    @Query("from LoanOrder o  where o.id between ?1 and ?2")
    List<LoanOrder> findOrdersByIdBetween(int lower, int upper);

    /**
     * 模糊查询 jpql
     * @return java.util.List<cn.hrfax.webflux.locusc.LoanOrder>
     */
    @Query("from LoanOrder o  where o.orderType like '%CAR%'")
    List<LoanOrder> findOrderLikeType();

}
