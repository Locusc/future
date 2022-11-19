package cn.locusc.sharding.sphere.jdbc.hint;

import org.apache.shardingsphere.api.sharding.hint.HintShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.hint.HintShardingValue;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author Jay
 * 自定义强制路由算法
 * 2022/11/17
 */
public class CustomHintShardingAlgorithm implements HintShardingAlgorithm<Long> {

    /**
     * 简单的强制路由
     * @param collection 强制路由地址
     * @param hintShardingValue HintManager传入的参数
     * @return java.util.Collection<java.lang.String>
     */
    @Override
    public Collection<String> doSharding(Collection<String> collection, HintShardingValue<Long> hintShardingValue) {
        return collection.stream()
                .flatMap(fm -> hintShardingValue.getValues()
                        .stream()
                        .filter(f -> fm.endsWith(String.valueOf(f % 2)))
                        .map(m -> fm))
                .collect(Collectors.toList());
    }

}
