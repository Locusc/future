package cn.locusc.seata.points.service.impl;

import cn.locusc.seata.points.entity.Points;
import cn.locusc.seata.points.mapper.PointsMapper;
import cn.locusc.seata.points.service.PointsService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.seata.rm.tcc.api.BusinessActionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 会员积分服务
 */
@Slf4j
@Service
public class PointsServiceImpl extends ServiceImpl<PointsMapper, Points> implements PointsService {

    @Autowired
    private PointsMapper pointsMapper;

    /**
     * 会员增加积分
     * @param username 用户名
     * @param points   增加的积分
     */
    public void increase(String username, Integer points) {
        QueryWrapper<Points> wrapper = new QueryWrapper<Points>();
        wrapper.lambda().eq(Points::getUsername, username);
        Points userPoints = this.getOne(wrapper);
        if (userPoints == null) {
            userPoints = new Points();
            userPoints.setUsername(username);
            // userPoints.setPoints(points);  不直接增加积分
            userPoints.setFrozenPoints(points);//设置冻结积分
        } else {
            // userPoints.setPoints(userPoints.getPoints() + points);
            userPoints.setFrozenPoints(points);
        }
        this.saveOrUpdate(userPoints);
    }

    @Override
    public boolean increaseCommit(BusinessActionContext context) {
        //查询⽤户积分
        QueryWrapper<Points> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Points::getUsername,
                context.getActionContext("username"));
        Points userPoints = this.getOne(wrapper);
        if (userPoints != null) {
            //增加⽤户积分
            userPoints.setPoints(userPoints.getPoints() +
                    userPoints.getFrozenPoints());
            //冻结积分清零
            userPoints.setFrozenPoints(0);
            this.saveOrUpdate(userPoints);
        }
        log.info("--------->xid=" + context.getXid() + " 提交成功!");
        return true;
    }

    @Override
    public boolean increaseRollback(BusinessActionContext context) {
        //查询⽤户积分
        QueryWrapper<Points> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Points::getUsername,
                context.getActionContext("username"));
        Points userPoints = this.getOne(wrapper);
        if (userPoints != null) {
            //冻结积分清零
            userPoints.setFrozenPoints(0);
            this.saveOrUpdate(userPoints);
        }
        log.info("--------->xid=" + context.getXid() + " 回滚成功!");
        return true;
    }

}
