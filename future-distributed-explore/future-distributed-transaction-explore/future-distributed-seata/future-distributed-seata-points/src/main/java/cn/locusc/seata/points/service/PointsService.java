package cn.locusc.seata.points.service;


import cn.locusc.seata.points.entity.Points;
import com.baomidou.mybatisplus.extension.service.IService;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

@LocalTCC
public interface PointsService extends IService<Points> {

    // void increase(String username, Integer points);

    @TwoPhaseBusinessAction(name = "increaseTcc", commitMethod =
            "increaseCommit"
            , rollbackMethod = "increaseRollback")
    void increase(@BusinessActionContextParameter(paramName =
            "username") String username,
                  @BusinessActionContextParameter(paramName =
                          "points") Integer points);
    boolean increaseCommit(BusinessActionContext context);

    boolean increaseRollback(BusinessActionContext context);

}

