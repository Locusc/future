package cn.locusc.seata.storage.service;

import cn.locusc.seata.storage.entity.Storage;
import com.baomidou.mybatisplus.extension.service.IService;
import io.seata.rm.tcc.api.BusinessActionContext;
import io.seata.rm.tcc.api.BusinessActionContextParameter;
import io.seata.rm.tcc.api.LocalTCC;
import io.seata.rm.tcc.api.TwoPhaseBusinessAction;

/**
 * 仓库服务
 */
@LocalTCC
public interface StorageService extends IService<Storage> {

    // public void decrease(Integer goodsCode, Integer quantity);

    @TwoPhaseBusinessAction(
            name = "decreaseTcc",
            commitMethod = "decreaseCommit",
            rollbackMethod = "decreaseRollback"
    )
    void decrease(@BusinessActionContextParameter(paramName = "goodsId") Integer goodsId,
                  @BusinessActionContextParameter(paramName = "quantity") Integer quantity);

    boolean decreaseCommit(BusinessActionContext context);

    boolean decreaseRollback(BusinessActionContext context);
}
