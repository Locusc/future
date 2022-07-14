package chapter3.codelist;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @author Jay
 * 基于双重检查锁定的错误单例模式实现
 *
 * 这种方法实现的单例模式固然是线程安全的，但是这意味着getInstance()的任何一个执行线程都需要申请锁。
 * 为了避免锁的开销，人们想到一个“聪明”的方法:在执行如清单3-15所示的临界区代码前先检查instance是否为null;若instance不为null,
 * 则 getInstance()直接返回，否则才执行临界区。由于这种方法实现的 getInstance()会两次检查instance的值是否为null，
 * 因此它被称为双重检查锁定( Double-checked Locking，DCL ) 15，如清单3-16所示。
 *
 * 双重检查锁定这种方法目前已经被视为反模式(Anti-Pattern )，即不再提倡使用的方法。但是，不少
 * 现有系统和框架（如Spring框架）还在使用这种方法，因此掌握这种方法可能失效的原因以及正确实现的办法仍然具有实际意义。
 * 有关该方法的进一步信息参见:The "Double-Checked Locking isBroken" Declaration，
 * http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html。
 * 2022/7/4
 */
public class IncorrectDCLSingleton316 {

    // 保存该类的唯一实例
    private static IncorrectDCLSingleton316 instance = null;

    /*
     * 私有构造器使其他类无法直接通过new创建该类的实例
     */
    private IncorrectDCLSingleton316() {
        // 什么也不做
    }

    /**
     * 创建并返回该类的唯一实例 <BR>
     * 即只有该方法被调用时该类的唯一实例才会被创建
     */
    @SuppressFBWarnings(value = "DC_DOUBLECHECK",
            justification = "此处特意使用双重检查锁定")
    public static IncorrectDCLSingleton316 getInstance() {
        if (null == instance) {// 操作①：第1次检查
            synchronized (IncorrectDCLSingleton316.class) {
                if (null == instance) {// 操作②：第2次检查
                    instance = new IncorrectDCLSingleton316();// 操作③
                }
            }
        }
        return instance;
    }

    public void someService() {
        // 省略其他代码
    }

}
