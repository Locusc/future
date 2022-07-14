package chapter3.codelist;

import utils.Debug;

import java.util.Map;

/**
 * @author Jay
 * 在启动工作者线程时实现对象安全发布范例
 *
 * 安全发布就是指对象以一种线程安全的方式被发布。
 * 当一个对象的发布出现我们不期望的结果或者对象发布本身不是我们所期望的时候，我们就称该对象逸出 (Escape )。
 * 逸出应该是我们要尽量避免的，因为它不是一种安全发布。
 * 上述的发布形式3（创建内部类，使得当前对象this能够被这个内部类使用)是最容易导致对象逸出的一种发布，它具体包括以下几种形式。
 * ·在构造器中将this赋值给一个共享变量。
 * ·在构造器中将this 作为方法参数传递给其他方法。
 * ·在构造器中启动基于匿名类的线程。
 *
 * 由于构造器未执行结束意味着相应对象的初始化未完成,因此在构造器中将this关键字代表的当前对象发布到其他线程会导致这些线程看到的可能是一个未初始化完毕的对象，
 * 从而可能导致程序运行结果错误。
 * 一般地，如果一个类需要创建自己的工作者线程，那么我们可以为该类定义一个init方法（可以是private的)，
 * 相应的工作者线程可以在该方法或者该类的构造器创建，但是线程的启动则是在init方法中执行的。
 * 然后我们再为该类定义一个静态方法(工厂方法),该工厂方法会创建该类的实例并调用该实例的init方法，如清单3-29所示。
 *
 * 2022/7/12
 */
public class SafeObjPublishWhenStartingThread {

    private final Map<String, String> objectState;

    private SafeObjPublishWhenStartingThread(Map<String, String> objectState) {
        this.objectState = objectState;
        // 不在构造器中启动工作者线程，以避免this逸出
    }


    private void init() {
        // 创建并启动工作者线程
        new Thread() {
            @Override
            public void run() {
                // 访问外层类实例的状态变量
                String value = objectState.get("someKey");
                Debug.info(value);
                // 省略其他代码
            }
        }.start();
    }

    // 工厂方法
    public static SafeObjPublishWhenStartingThread newInstance(Map<String, String> objState) {
        SafeObjPublishWhenStartingThread instance = new SafeObjPublishWhenStartingThread(objState);
        instance.init();
        return instance;
    }

}
