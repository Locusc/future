package chapter7.codelist.diningphilosophers;

import utils.Debug;

import java.lang.reflect.Constructor;

/**
 * @author Jay
 * 哲学家就餐问题模拟程序
 * 2022/7/19
 */
public class DiningPhilosopherProblem74 {

    public static void main(String[] args) throws Exception {
        int numOfPhilosopers;
        numOfPhilosopers = args.length > 0 ? Integer.valueOf(args[0]) : 2;
        // 创建筷子
        Chopstick72[] chopsticks = new Chopstick72[numOfPhilosopers];
        for (int i = 0; i < numOfPhilosopers; i++) {
            chopsticks[i] = new Chopstick72(i);
        }

        String philosopherImplClassName = System.getProperty("x.philo.impl");
        if (null == philosopherImplClassName) {
            philosopherImplClassName = "FixedPhilosopher77";
        }

        Debug.info("Using %s as implementation.", philosopherImplClassName);
        for (int i = 0; i < numOfPhilosopers; i++) {
            // 创建哲学家
            createPhilosopher(philosopherImplClassName, i, chopsticks);
        }
    }

    private static void createPhilosopher(String philosopherImplClassName,
                                          int id, Chopstick72[] chopsticks) throws Exception {

        int numOfPhilosopers = chopsticks.length;
        @SuppressWarnings("unchecked")
        Class<Philosopher> philosopherClass = (Class<Philosopher>) Class
                .forName(DiningPhilosopherProblem74.class.getPackage().getName() + "."
                        + philosopherImplClassName);
        Constructor<Philosopher> constructor = philosopherClass.getConstructor(
                int.class, Chopstick72.class, Chopstick72.class);
        Philosopher philosopher = constructor.newInstance(id, chopsticks[id],
                chopsticks[(id + 1)
                        % numOfPhilosopers]);
        philosopher.start();
    }

}
