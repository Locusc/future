package cn.locusc.mtia.chapter7.codelist.case01;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Jay
 * 配置助手.
 * 该类可能导致死锁！
 * 2022/7/19
 */
public enum ConfigurationHelper710 implements ConfigEventListener{

    INSTANCE;

    final ConfigurationManager79 configManager;
    final ConcurrentMap<String, Configuration711> cachedConfig;

    private ConfigurationHelper710() {
        configManager = ConfigurationManager79.INSTANCE;
        cachedConfig = new ConcurrentHashMap<>();
    }

    public Configuration711 getConfig(String name) {
        Configuration711 cfg;
        cfg = getCachedConfig(name);
        if (null == cfg) {
            synchronized (this) {
                cfg = getCachedConfig(name);
                if (null == cfg) {
                    cfg = configManager.load(name);
                    cachedConfig.put(name, cfg);
                }
            }
        }
        return cfg;
    }

    public Configuration711 getCachedConfig(String name) {
        return cachedConfig.get(name);
    }

    public ConfigurationHelper710 init() {
        configManager.registerListener(this);
        return this;
    }

    @Override
    public void onConfigLoaded(Configuration711 cfg) {
        cachedConfig.putIfAbsent(cfg.getName(), cfg);
    }

    @Override
    public void onConfigUpdated(String name, int newVersion,
                                Map<String, String> properties) {
        Configuration711 cachedConfig = getCachedConfig(name);
        // 更新内容和版本这两个操作必须是原子操作
        synchronized (this) {
            if (null != cachedConfig) {
                cachedConfig.update(properties);
                cachedConfig.setVersion(newVersion);
            }
        }
    }

}
