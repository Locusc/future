package cn.locusc.mtia.chapter7.codelist.case01;

import java.util.Map;

/**
 * 事件监听器接口.
 */
public interface ConfigEventListener {

    void onConfigLoaded(Configuration711 cfg);

    void onConfigUpdated(String name, int newVersion,
                         Map<String, String> properties);

}
