package cn.locusc.sharding.sphere.jdbc.key;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.core.strategy.keygen.SnowflakeShardingKeyGenerator;
import org.apache.shardingsphere.spi.keygen.ShardingKeyGenerator;

import java.util.Properties;

@Slf4j
public class CustomKeyGenerator implements ShardingKeyGenerator {

    private final SnowflakeShardingKeyGenerator snowflakeShardingKeyGenerator = new SnowflakeShardingKeyGenerator();

    @Override
    public Comparable<?> generateKey() {
        log.info("execute snowflakeShardingKeyGenerator");
        return snowflakeShardingKeyGenerator.generateKey();
    }

    @Override
    public String getType() {
        return "CustomKeyGenerator";
    }

    @Override
    public Properties getProperties() {
        return null;
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
