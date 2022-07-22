package chapter7.codelist.case01;

import utils.Tools;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Jay
 * 配置管理器.
 * 该类可能导致死锁！
 * 2022/7/19
 */
public enum ConfigurationManager79 {

    INSTANCE;

    protected final Set<ConfigEventListener> listeners = new HashSet<>();

    public Configuration711 load(String name) {
        Configuration711 cfg = loadConfigurationFromDB(name);
        synchronized (this) {
            for (ConfigEventListener listener : listeners) {
                listener.onConfigLoaded(cfg);
            }
        }
        return cfg;
    }

    // 从数据库加载配置实体（数据）
    private Configuration711 loadConfigurationFromDB(String name) {
        // 模拟从数据库加载配置数据
        Tools.randomPause(50);
        Configuration711 cfg = new Configuration711(name, 0);
        cfg.setProperty("url", "https://github.com/Viscent");
        cfg.setProperty("connectTimeout", "2000");
        cfg.setProperty("readTimeout", "2000");
        return cfg;
    }

    public synchronized void registerListener(ConfigEventListener listener) {
        listeners.add(listener);
    }

    public synchronized void removeListener(ConfigEventListener listener) {
        listeners.remove(listener);
    }

    public synchronized void update(String name, int newVersion,
                                    Map<String, String> properties) {
        for (ConfigEventListener listener : listeners) {
            // 这个外部方法调用可能导致死锁！
            listener.onConfigUpdated(name, newVersion, properties);
        }
    }
}
