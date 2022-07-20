CountDownLatch可以用来实现一个（或者多个）线程等待其他线程完成一组特定的操作之后才继续运行。这组操作被称为先决操作。
CountDownLatch 内部会维护一个用于表示未完成的先决操作数量的计数器。
CountDownLatch.countDown()每被执行一次就会使相应实例的计数器值减少1。
CountDownLatch.await()相当于一个受保护方法，其保护条件为“计数器值为0”(代表所有先决操作已执行完毕)，
目标操作是一个空操作。因此，当计数器值不为О时CountDownLatch.await()的执行线程会被暂停,
这些线程就被称为相应CountDownLatch 上的等待线程。CountDownLatch.countDown()相当于一个通知方法，
它会在计数器值达到О的时候唤醒相应实例上的所有等待线程。计数器的初始值是在CountDownLatch 的构造参数中指定的,如下声明所示:

public countDownLatch (int count)

count参数用于表示先决操作的数量或者需要被执行的次数。
当计数器的值达到0之后，该计数器的值就不再发生变化。
此时，调用CountDownLatch.countDown()并不会导致异常的抛出，
并且后续执行CountDownLatch.await()的线程也不会被暂停。
因此，CountDownLatch的使用是一次性的:一个CountDownLatch实例只能够实现一次等待和唤醒。

可见，CountDownLatch内部封装了对“全部先决操作已执行完毕”(计数器值为О)这个保护条件的等待与通知的逻辑，
因此客户端代码在使用CountDownLatch 实现等待/通知的时候调用await、countDown方法都无须加锁。

下面看一个实战案例。某定制 Web服务器（以下称其为服务器）在其启动时需要启动若干启动过程比较耗时的服务（以下称其为服务)。
为了尽可能地减少服务器启动过程的总耗时，该服务器会使用专门的工作者线程以并发的方式去启动这些服务。
但是，服务器在所有启动操作结束后，需要判断这些服务的状态以检查服务器的启动是否是成功的。
只有在这些服务全部启动成功的情况下该服务器才被认为是启动成功的,否则服务器的启动算失败，此时服务器会自动终止(退出Java虚拟机)，如清单5-4所示。

注意
CountDownLatch内部计数器值达到О后其值就恒定不变，后续执行该CountDownLatch实例的await方法的任何一个线程都不会被暂停。
为了避免等待线程永远被暂停，CountDownLatch.countDown()调用必须放在代码中总是可以被执行到的地方，例如finally块中。
对于同一个CountDownLatch实例 latch，
latch.countDown()的执行线程在执行该方法之前所执行的任何内存操作对等待线程在latch.await()调用返回之后的代码是可见的且有序的。

