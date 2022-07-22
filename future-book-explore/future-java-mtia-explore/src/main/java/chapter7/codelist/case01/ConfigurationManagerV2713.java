package chapter7.codelist.case01;

import utils.Tools;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Jay
 * 使用开放调用改造 ConfigurationManager
 *
 * 本案例中出现的死锁可以使用“开放调用”来规避。
 * 所谓开放调用( Open Call)就是一个方法在调用外部方法(Alien Method，包括其他类的方法以及当前类的可覆盖方法)的时候不持有任何锁。
 * 显然，开放调用能够消除死锁产生的必要条件中的“持有并等待资源”。
 * 既然通过上面的分析我们已经锁定了本案例中导致死锁的“罪魁祸首”—ConfigurationManager.update方法(参见清单7-9)
 * 以及ConfigurationHelper.getConfig方法(参见清单7-10),那么我们只需要将这两个方法对外部方法的调用改为开放调用即可。
 * 考虑到将ConfigurationHelper.getConfig 方法改造为开放调用比较困难，我们不妨从ConfigurationManager 入手——
 * 将ConfigurationManager的实例变量listeners改用线程安全的 Set接口实现类 CopyOnWriteArraySet(参见第6章)，
 * 这种改造使得我们可以将ConfigurationManager的几个方法，包括update方法和 load方法改为无须申请锁的方法，如清单7-13所示。
 * 改造后的ConfigurationManager.update方法对外部方法onConfigUpdated的调用已经是开放调用
 * （类似地，load 方法对外部方法 onConfigLoaded 的调用也是开放调用)。
 * 尽管改造之后ConfigurationHelper.getConfig 方法对ConfigurationManager.load 方法的调用仍然不是开放调用,
 * 但是由于ConfigurationManager中所有对ConfigurationHelper的方法调用都不持有锁，
 * 因此死锁产生的必要条件中的“循环等待”就不会成立，由此我们还是规避了死锁。
 * 2022/7/19
 */
public enum ConfigurationManagerV2713 {

    INSTANCE;
    protected final Set<ConfigEventListener> listeners;
    {
        listeners = new CopyOnWriteArraySet<>();
    }

    public Configuration711 load(String name) {
        Configuration711 cfg = loadConfigurationFromDB(name);
        for (ConfigEventListener listener : listeners) {
            listener.onConfigLoaded(cfg);
        }
        return cfg;
    }

    private Configuration711 loadConfigurationFromDB(String name) {
        // 模拟从数据库加载配置数据
        Tools.randomPause(50);
        Configuration711 cfg = new Configuration711(name, 0);
        cfg.setProperty("url", "https://github.com/Viscent");
        cfg.setProperty("connectTimeout", "2000");
        cfg.setProperty("readTimeout", "2000");
        return cfg;
    }

    public void registerListener(ConfigEventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ConfigEventListener listener) {
        listeners.remove(listener);
    }

    public void update(String name, int newVersion,
                       Map<String, String> properties) {
        for (ConfigEventListener listener : listeners) {
            listener.onConfigUpdated(name, newVersion, properties);
        }
    }

}
