package cn.locusc.seata.storage.service.impl;

import cn.locusc.seata.storage.entity.Storage;
import cn.locusc.seata.storage.mapper.StorageMapper;
import cn.locusc.seata.storage.service.StorageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 仓库服务
 */
@Slf4j
@Service
public class StorageServiceImpl extends ServiceImpl<StorageMapper, Storage> implements StorageService {

    /**
     * 减少库存
     *
     * @param goodsId  商品ID
     * @param quantity 减少数量
     * @return 库存对象
     */
    public void decrease(Integer goodsId, Integer quantity) {
        QueryWrapper<Storage> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Storage::getGoodsId, goodsId);
        Storage goodsStorage = this.getOne(wrapper);
        if (goodsStorage.getStorage() >= quantity) {
            // goodsStorage.setStorage(goodsStorage.getStorage() - quantity);
            //设置冻结库存
            goodsStorage.setFrozenStorage(quantity);
        } else {
            throw new RuntimeException(goodsId + "库存不足,目前剩余库存:" + goodsStorage.getStorage());
        }
        this.saveOrUpdate(goodsStorage);
    }

    @Override
    public boolean decreaseCommit(BusinessActionContext context) {
        QueryWrapper<Storage> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Storage::getGoodsId,
                context.getActionContext("goodsId"));
        Storage goodsStorage = this.getOne(wrapper);
        if (goodsStorage != null) {
            //扣减库存
            goodsStorage.setStorage(goodsStorage.getStorage() -
                    goodsStorage.getFrozenStorage());
            //冻结库存清零
            goodsStorage.setFrozenStorage(0);
            this.saveOrUpdate(goodsStorage);
        }
        log.info("--------->xid=" + context.getXid() + " 提交成功!");
        return true;
    }

    @Override
    public boolean decreaseRollback(BusinessActionContext context) {
        QueryWrapper<Storage> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Storage::getGoodsId,
                context.getActionContext("goodsId"));
        Storage goodsStorage = this.getOne(wrapper);
        if (goodsStorage != null) {
            //冻结库存清零
            goodsStorage.setFrozenStorage(0);
            this.saveOrUpdate(goodsStorage);
        }
        log.info("--------->xid=" + context.getXid() + " 回滚成功!");
        return true;
    }
}
