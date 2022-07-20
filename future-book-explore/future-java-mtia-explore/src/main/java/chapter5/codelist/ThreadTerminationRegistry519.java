package chapter5.codelist;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Jay
 * 线程终止登记表源码
 * 2022/7/15
 */
public enum  ThreadTerminationRegistry519 {

    INSTANCE;

    private final Set<Handler> handlers = new HashSet<>();

    public synchronized void register(Handler handler) {
        handlers.add(handler);
    }

    public void clearThreads() {
        final Set<Handler> handlersSnapshot;
        // 为保障线程安全，在遍历时将handlers复制一份
        synchronized (this) {
            handlersSnapshot = new HashSet<>(handlers);
        }

        for (Handler handler : handlersSnapshot) {
            try {
                handler.terminate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 线程终止处理器
     * <p>
     * 封装了有关线程停止的知识
     *
     * @author Viscent Huang
     */
    public static interface Handler {
        void terminate();
    }

}
