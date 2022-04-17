package cn.locusc.project.reactor.jpa.crud.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

/**
 * @author Jay
 * order entity
 * 2022/3/20
 */
@Data
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "loan_order")
public class LoanOrder {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "order_type")
    private String orderType;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "order_sum")
    private double orderSum;


    public LoanOrder(String orderNo, String orderType, Integer userId, double orderSum) {
        this.orderNo = orderNo;
        this.orderType = orderType;
        this.userId = userId;
        this.orderSum = orderSum;
    }

}
