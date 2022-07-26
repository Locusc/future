事实上，我们接触到的能够导致死锁的代码可能并不直接具备图7-3所示的特征(持有一个锁并申请另外一个锁的特征),
更为常见的情况可能是一个方法在持有一个锁的情况下调用一个外部方法( Alien Method，参见3.11节)。
设类ClassA有两个同步方法 syncOperationA和syncOperationB，类ClassB也有两个同步方法 syncOperationC和 syncOperationD，
syncOperationA 会调用syncOperationC，syncOperationD会调用syncOperationB。
这里，syncOperationA和 syncOperationD这两个方法虽然不直接具备图7-3所示的特征，但是由于它们调用了一个外部方法，
而这个方法是一个同步方法,因此这两个方法实际上具备了图7-3所示的特征。当一个线程在执行ClassA.syncOperationA()时，
另外一个线程正在执行ClassB.syncOperationD()，那么这两个线程就有可能产生死锁。
一般地，一个方法在持有一个锁的情况下调用一个外部方法，而外部方法往往不在我们(开发人员)的控制范围之内，其自身可能不会申请另外一个锁，
也可能会申请另外一个锁。因此，在持有一个锁的情况下调用一个外部方法的代码很可能会间接具备图7-3所示的特征，
从而导致死锁。这种情形导致死锁在一些开源软件甚至于Java标准库本身都曾出现过。

下面我们通过一个实战案例来讲解在持有一个锁的情况下调用外部方法导致的死锁。
某系统的实际配置数据存储在数据库之中，该系统有一个配置管理模块，其核心功能是为业务模块提供获取以及缓存系统配置数据的功能、
为系统管理模块提供动态更新系统配置数据的功能。该模块的主要类如表7-2所示。

表7-2某系统配置管理模块的主要类
Configuration: 配置实体，代表该系统的配置数据。每个配置实体可以包含若干配置条目。每个配置条目是一个从“属性名”到“属性值”的关联, 清单7-11
ConfigurationHelper: 配置助手。业务模块通过调用该类的相应方法访问系统的配置实体。该类还能够缓存配置实体, 清单7-10
ConfigurationManager: 配置管理器。系统管理模块在更新完系统的配置数据后通过该类将这种更新“通知”到ConfigurationHelper 以及需要的业务模块对象, 清单7-9

由清单7-9可见，ConfigurationManager.update方法在持有一个锁( ConfigurationManager当前实例对应的内部锁）
的情况下调用了一个外部方法—-ConfigEventListener.onConfigUpdated方法。
而 ConfigurationHelper(见清单7-10)作为ConfigEventListener接口的一个实现类，
其 onConfigUpdated方法内部又申请另外一个锁( ConfigurationHelper当前实例对应的内部锁)。
可见，ConfigurationManager.update方法间接具备了图7-3所示的代码特征。
另一方面，我们不难发现ConfigurationHelper.getConfig方法事实上也具备图7-3所示的代码特征:
ConfigurationHelper.getConfig方法可能在持有一个锁（ConfigurationHelper当前实例对应的内部锁）的情况下调用外部方法——
ConfigurationManager.load方法,而这个方法本身会申请另外一个锁( ConfigurationManager当前实例对应的内部锁)。
因此，如清单 7-12所示，假如一个业务线程执行ConfigurationHelper.getConfig方法来获取一个配置实体的时候，
另外一个线程（配置管理线程）恰好更新了系统的配置数据，该线程通过执行ConfigurationManager.update
方法将这种更新“通知”给ConfigurationHelper 以及可能的业务模块对象，那么这两个线程就可能产生死锁。

提示,规避死锁的常见方法:
·粗锁法（Coarsen-grained Lock )——使用一个粗粒度的锁代替多个锁。
·锁排序法（Lock Ordering )——相关线程使用全局统一的顺序申请锁。
·使用ReentrantLock.tryLock(long,TimeUnit)来申请锁。
·使用开放调用（Open Call ) ——在调用外部方法时不加锁。
·使用锁的替代品。
规避死锁的另外一种“终极”方法就是不使用锁!第6章以及前面章节我们介绍了一些锁的替代品（无状态对象、线程特有对象以及volatile关键字等)。
在条件允许的情况下使用这些替代品在保障线程安全的前提下不仅能够避免锁的开销，还能够直接避免死锁!







