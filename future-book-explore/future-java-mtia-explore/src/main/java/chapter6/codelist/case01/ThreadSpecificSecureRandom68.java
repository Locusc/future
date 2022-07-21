package chapter6.codelist.case01;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author Jay
 * 使用ThreadLocal避免锁的争用
 *
 * 由于线程安全的对象内部往往需要使用锁，因此，多个线程共享线程安全的对象可能导致锁的争用。
 * 所以，有时候为了避免锁的争用导致的开销（主要是上下文切换)，我们也特意将线程安全的对象作为线程特有对象来使用，
 * 从而既避免了锁的开销，又减少了对象创建的次数。
 * 下面看一个 ThreadLocal实战案例。某系统会在用户执行某些关键操作前通过短信验证码(一个6位数字组成的字符串)来验证操作者的身份，
 * 以确定是否是用户本人进行操作的（而不是他人冒充进行操作的)。这个验证码是随机生成的，为了尽量保障这个验证码的随机性，
 * 我们使用强随机数生成器java.security.SecureRandom(它是 Random的一个子类)。尽管SecureRandom是线程安全的，
 * 并因此可以被多个线程共享，但是为了避免多个线程共享SecureRandom实例可能导致的对SecureRandom内部所使用锁的争用，我们
 * 决定不在多个线程间共享同一SecureRandom实例。另外，考虑到每次生成验证码的时候都创建一个SecureRandom也是不现实的（开销太大)，
 * 因此我们决定将SecureRandom实例作为一个线程特有对象来使用。该案例中用于生成验证码的随机数生成器如清单6-8所示。
 *
 * ThreadSpecificSecureRandom通过线程局部变量来引用SecureRandom 实例,
 * 这使得执行nextInt方法以生成验证码的多个线程各自使用各自的SecureRandom 实例,从而避免了锁的争用。
 * JDK 1.7中引入的标准库类java.util.concurrent.ThreadLocalRandom的初衷与该案例所要实现的目标相似。
 * ThreadLocalRandom也是Random 的一个子类，它相当于ThreadLocal<Random>。不过，,ThreadLocalRandom所产生的随机数并非强随机数。
 *
 * 4由于SecureRandom的内部实现可能涉及多个SecureRandom实例从同一个嫡池（Entropy Pool )中获
 * 取随机数生成器所需的种子( Seed ),因此系统中创建的SecureRandom实例越多则嫡池中嫡(Entropy )不够用的概率就越大，
 * 当系统中的嫡不够用时，那么获取嫡的线程就会被阻塞。
 * 因此，在工作者线程数较大的情况下以线程特有对象的方式来使用SecureRandom需要注意系统中嫡的数量的有限性。
 *
 * JDK 1.7中引入的标准库类java.util.concurrent.ThreadLocalRandom的初衷与该案例所要实现的目标相似。
 * ThreadLocalRandom也是 Random 的一个子类，它相当于ThreadLocal<Random>。不过，ThreadLocalRandom所产生的随机数并非强随机数。
 *
 * 2022/7/18
 */
public enum  ThreadSpecificSecureRandom68 {

    INSTANCE;

    final static ThreadLocal<SecureRandom> SECURE_RANDOM = new ThreadLocal<SecureRandom>() {
        @Override
        protected SecureRandom initialValue() {
            SecureRandom srnd;
            try {
                srnd = SecureRandom.getInstance("SHA1PRNG");
            } catch (NoSuchAlgorithmException e) {
                srnd = new SecureRandom();
                new RuntimeException("No SHA1PRNG available,defaults to new SecureRandom()", e)
                        .printStackTrace();
            }
            // 通过以下调用来初始化种子
            srnd.nextBytes(new byte[20]);
            return srnd;
        }
    };

    // 生成随机数
    public int nextInt(int upperBound) {
        SecureRandom secureRnd = SECURE_RANDOM.get();
        return secureRnd.nextInt(upperBound);
    }

    public void setSeed(long seed) {
        SecureRandom secureRnd = SECURE_RANDOM.get();
        secureRnd.setSeed(seed);
    }

}
