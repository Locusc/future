package chapter7.codelist.diningphilosophers;

import utils.Debug;
import utils.Tools;

/**
 * @author Jay
 * 表示哲学家的抽象类
 *
 * 由于筷子的数量小于哲学家的人数的两倍，因此筷子可被看作哲学家（线程)的共享资源。
 * 这里，我们选择用一个非线程安全的类Chopstick表示筷子，如清单7-2所示。
 *
 * 2022/7/19
 */
public abstract class AbstractPhilosopher71 extends Thread implements Philosopher {

    protected final int id;
    protected final Chopstick72 left;
    protected final Chopstick72 right;

    public AbstractPhilosopher71(int id, Chopstick72 left, Chopstick72 right) {
        super("Philosopher-" + id);
        this.id = id;
        this.left = left;
        this.right = right;
    }

    @Override
    public void run() {
        for (;;) {
            think();
            eat();
        }
    }

    /*
     * @see io.github.viscent.mtia.ch7.diningphilosophers.Philosopher#eat()
     */
    @Override
    public abstract void eat();

    protected void doEat() {
        Debug.info("%s is eating...%n", this);
        Tools.randomPause(10);
    }

    /*
     * @see io.github.viscent.mtia.ch7.diningphilosophers.Philosopher#think()
     */
    @Override
    public void think() {
        Debug.info("%s is thinking...%n", this);
        Tools.randomPause(10);
    }

    @Override
    public String toString() {
        return "Philosopher-" + id;
    }

}
