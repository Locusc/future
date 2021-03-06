- 1.走近JAVA世界中的线程
- 2.多线程编程的目标与挑战
- 3.JAVA线程同步机制
- 4.小试牛刀: 玩转线程
- 5.线程间的协作
- 6.保障线程安全的设计技术
- 7.线程的活性故障
- 8.线程管理
- 9.JAVA异步线程
- 10.JAVA多线程程序的调试与测试
- 11.多线程编程的硬件基础与JAVA内存模型
- 12.JAVA多线程程序的性能校验

https://github.com/Viscent/javamtia/tree/master/JavaMultiThreadInAction

objRef = allocate(IncorrectDCLSingleton.class); //子操作①:分配对象所需的存储空间
invokeConstructor(objRef); //子操作②:初始化objRef引用的对象
instance = objRef; //子操作③:将对象引用写入共享变量

<刷新处理器缓存和冲刷处理器缓存的作用>
前一个动作保证了该锁的当前持有线程能够读取到前一个持有线程对这些数
据所做的更新，后一个动作保证了该锁的持有线程对这些数据所做的更新对该锁的后续持
有线程可见

<锁如何保障可见性>
JAVA平台中，锁的获得隐含着刷新处理器缓存这个动作，
这使得读线程在执行临界区代码前（获得锁之后）可以将写线程对共享变量所做的更新同步到该线程
执行处理器的高速缓存中；而锁的释放隐含着冲刷处理器缓存这个动作，这使得写线程对
共享变量所做的更新能够被"推送"到该线程执行处理器的高速缓存中 ，从而对读线程可
同步 因此，锁能够保障可见性。

<什么是可重入锁>
可重入性（ Reentrancy ）描述这样一个问题：一个线程在其持有一个锁的时候能否再
次（或者多次）申请该锁 如果一个线程持有一个锁的时候还能够继续成功申请该锁，那
么我们就称该锁是可重入的（ Reentrant ），否则我们就称该锁为非可重入的（ Non-reentrant）

<锁的粒度>
一个锁实例可以保护一个或者多个共享数据, 一个锁实例所保护的共享数据的数据量大小
就被称为该锁的粒度（Granularity）。一个锁实例保护的共享数据量大，我们就称该锁的粒度粗，否则就称该锁的粒度细。

<内部锁（仅支持非公平）>
Java 平台中的任何一个对象都有唯一一个与之关联的锁 这种锁被称为监视器
( Monitor ）或者 内部锁（ Intrinsic Lock）内部锁是一种排他锁，它能够保障原子性 、可
性和有序性
内部锁是通过 synchronized 关键字实现的，synchronized 关键字可以用来修饰方法以
及代码块（花括号“｛｝”包裹的代码）
内部锁的申请和释放由JVM代为实施(这也是为什么synchronized被称为内部锁)

<显式锁（支持公平和非公平）>
显式锁是自JDK 1.5开始引人的排他锁。作为一种线程同步机制，其作用与内部锁相同。
它提供了一些内部锁所不具备的特性,但并不是内部锁的替代品。
显式锁（Explicit Lock )是java.util.concurrent.lcoks.Lock接口的实例。
该接口对显式锁进行了抽象，其定义的方法如图3-2所示。类java.util.concurrent.lcoks.ReentrantLock 是Lock接口的默认实现类。

<公平锁与非公平锁>
公平锁：线程调度器依次调度, 都进行唤醒操作
非公平锁：算法支持, 可能直接是RUNNABLE状态线程直接获取或者是等待队列中的线程进行唤醒。

<公平锁与非公平锁的调度>
公平锁保障锁调度的公平性往往是以增加了线程的暂停和唤醒的可能性,即增加了上下文切换为代价的。
因此，公平锁适合于锁被持有的时间相对长或者线程申请锁的平均间隔时间相对长的情形。
总的来说使用公平锁的开销比使用非公平锁的开销要大，因此显式锁默认使用的是非公平调度策略。

<volatile关键字>
volatile字面有“易挥发”的意思，引申开来就是有“不稳定”的意思。
volatile关键字用于修饰共享可变变量，即没有使用final关键字修饰的实例变量或静态变量，相应的变量就被称为volatile变量，如下所示:
private volatile int logLevel;
volatile关键字表示被修饰的变量的值容易变化（即被其他线程更改)，因而不稳定。
volatile变量的不稳定性意味着对这种变量的读和写操作都必须从高速缓存或者主内存(也是通过高速缓存读取）中读取，
以读取变量的相对新值。因此,volatile变量不会被编译器分配到寄存器进行存储，
对volatile变量的读写操作都是内存访问（访问高速缓存相当于主内存）操作。
volatile关键字常被称为轻量级锁，其作用与锁的作用有相同的地方:保证可见性和有序性。
所不同的是，在原子性方面它仅能保障写volatile变量操作的原子性，但没有锁的排他性;
其次，volatile关键字的使用不会引起上下文切换(这是volatile被冠以“轻量级”的原因)。
因此，volatile更像是一个轻量级简易（功能比锁有限）锁。

<volatile关键字的作用>
volatile关键字的作用包括:保障可见性、保障有序性和保障long/double型变量读写操作的原子性。

<volatile关键字对原子性的约定>
page num 122
volatile关键字在原子性方面仅保障对被修饰的变量的读操作、写操作本身的原子性。
如果要保障对volatile变量的赋值操作的原子性，那么这个赋值操作不能涉及任何共享变量（包括被赋值的volatile变量本身）的访问。

<volatile关键字的注意点>
volatile关键字在可见性方面仅仅是保证读线程能够读取到共享变量的相对新值。
对于引用型变量和数组变量，volatile关键字并不能保证读线程能够读取到相应对象的字段（实例变量、静态变量)、元素的相对新值。

<CAS解读>
CAS(Compare and Swap)是对一种处理器指令(例如 x86 处理器中的cmpxchg指令)
的称呼, 不少多线程相关的Java标准库类的实现最终都会借助CAS
事实上，保障像自增这种比较简单的操作的原子性我们有更好的选择-CAS.
CAS能够将read-modify-write和check-and-act之类的操作转换为原子操作。

CAS仅保障共享变量更新操作的原子性，它并不保障可见性。

boolean compareAndSwap(Variable v,object A, Object B){
    if (A == v.get(){   // check:检查变量值是否被其他线程修改过
        v.set(B); // act:更新变量值
        return true; // 更新成功
    }
}
return false;//变量值已被其他线程修改，更新失败

<对象发布>
对象发布是指使对象能够被其作用域之外的线程访问。常见的对象发布形式除了上述的共享 private变量之外，还包括以下几种。

<对象逸出>
安全发布就是指对象以一种线程安全的方式被发布。
当一个对象的发布出现我们不期望的结果或者对象发布本身不是我们所期望的时候，我们就称该对象逸出(Escape)。
逸出应该是我们要尽量避免的,因为它不是一种安全发布。

一个对象在其初始化过程中没有出现 this逸出，我们就称该对象为正确创建的对象( Properly Constructed Object )。
要安全发布一个正确创建的对象，我们可以根据情况从以下几种方式中选择:
使用static关键字修饰引用该对象的变量。
使用final关键字修饰引用该对象的变量。
使用volatile关键字修饰引用该对象的变量。
使用AtomicReference来引用该对象。
对访问该对象的代码进行加锁。



