package cn.locusc.mtia.chapter8.codelist;

import cn.locusc.mtia.utils.Debug;
import cn.locusc.mtia.utils.Tools;

import java.io.File;
import java.util.concurrent.*;

/**
 * @author Jay
 * 获取线程池执行的任务处理结果demo
 *
 * 在上述例子中，客户端代码(TPBigFileDownloader )仅向线程池提交任务(文件下载子任务)而不关心这些任务的处理结果数据。
 * 如果客户端关心任务的处理结果，那么它可以使用ThreadPoolExecutor 的另外一个submit方法来提交任务，该submit方法的声明如下:
 * public <T> Future<T> submit (Callable<T> task)
 *
 * task参数代表客户端需要提交的任务，其类型为java.util.concurrent.Callable。Callable接口定义的唯一方法声明如下:
 * v call () throws Exception
 *
 * Callable接口也是对任务的抽象:任务的处理逻辑可以在Callable接口实现类的call方法中实现。
 * Callable接口相当于一个增强型的Runnable接口: call方法的返回值代表相应任务的处理结果，其类型V是通过Callable接口的类型参数指定的;
 * call方法代表的任务在其执行过程中可以抛出异常。而 Runnable接口中的run方法既无返回值也不能抛出异常。
 * Executors.callable(Runnable task,T result)能够将Runnable接口转换为Callable接口实例。
 *
 * 上述submit方法的返回值类型为java.util.concurrent.Future。
 * Future接口实例可被看作提交给线程池执行的任务的处理结果句柄(Handle )，
 * Future.get()方法可以用来获取task参数所指定的任务的处理结果,该方法声明如下:
 * v get () throws InterruptedException,ExecutionException
 *
 * Future.get()被调用时，如果相应的任务尚未执行完毕，那么Future.get()会使当前线程暂停，
 * 直到相应的任务执行结束（包括正常结束和抛出异常而终止)。因此，Future.get()是个阻塞方法，
 * 该方法能够抛出InterruptedException 说明它可以响应线程中断。另外，假设相应的任务执行过程中抛出一个任意的异常originalException，
 * 那么 Future.get()方法本身就会抛出相应的 ExecutionException异常。
 * 调用这个异常（ExecutionException)的getCause()方法可返回originalException。
 * 因此，客户端代码可以通过捕获Future.get()调用抛出的异常来知晓相应任务执行过程中抛出的异常。
 *
 * 由于在任务未执行完毕的情况下调用Future.get()方法来获取该任务的处理结果会导致等待并由此导致上下文切换，
 * 因此客户端代码应该尽可能早地向线程池提交任务，并尽可能晚地调用Future.get()方法来获取任务的处理结果,
 * 而线程池则正好利用这段时间来执行已提交的任务（包括我们关心的任务)。
 *
 * 注意
 * 客户端代码应该尽可能早地向线程池提交任务，并仅在需要相应任务的处理结果数据的那一刻才调用Future.get()方法。
 *
 * 下面看一个 Future接口使用的 Demo。该 Demo模拟从指定的车牌照片中识别出相应的车牌号(字符串)。
 * 这个识别的过程可能比较耗时，因此我们将这个识别任务封装为一个Callable实例提交给专门的线程池执行，
 * 并在需要该任务的处理结果数据（车牌号码)时才调用Future.get()，如清单8-7所示。
 *
 * Future接口还支持任务的取消。为此，Future接口定义了如下方法:
 * boolean cancel (boolean mayInterruptIfRunning)
 * 该方法的返回值表示相应的任务取消是否成功。任务取消失败的原因包括待取消的任务已执行完毕或者正在执行、已经被取消以及其他无法取消因素。
 * 参数mayInterruptIfRunning表示是否允许通过给相应任务的执行线程发送中断来取消任务。Future.isCancelled()返回值代表相应的任务是否被成功取消。
 * 由于一个任务被成功取消之后，相应的 Future.get()调用会抛出CancellationException异常（运行时异常)，
 * 因此如果任务有可能会被取消，那么在获取任务的处理结果之前，我们需要先判断任务是否已经被取消了。
 *
 * Future.isDone()方法可以检测相应的任务是否执行完毕。任务执行完毕、执行过程中抛出异常以及任务被取消都会导致该方法返回 true。
 *
 * Future.get()会使其执行线程无限制地等待，直到相应的任务执行结束。商用系统中这种无时间限制的等待往往是不现实的。
 * 此时我们可以使用get方法的另外一个版本，其声明如下:
 * v get (long timeout，TimeUnit unit) throws InterruptedException,ExecutionException,TimeoutException
 *
 * 该方法的作用与Future.get()相同,不过它允许我们指定一个等待超时时间。如果在该时间内相应的任务未执行结束，
 * 那么该方法就会抛出 TimeoutException。
 * 由于该方法参数中指定的超时时间仅仅用于控制客户端线程(即该方法的执行线程)等待相应任务的处理结果最多会等待多长时间，
 * 而非相应任务本身的执行时间限制，
 * 因此，客户端线程通常需要在捕获TimeoutException之后执行Future.cancel(true)来取消相应任务的执行(因为此时我们已经不再需要该任务的处理结果了)。
 *
 * 2022/7/20
 */
public class TaskResultRetrievalDemo87 {

    final static int N_CPU = Runtime.getRuntime().availableProcessors();

    final ThreadPoolExecutor executor = new ThreadPoolExecutor(0, N_CPU * 2, 4,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(100),
            new ThreadPoolExecutor.CallerRunsPolicy());

    public static void main(String[] args) {
        TaskResultRetrievalDemo87 demo = new TaskResultRetrievalDemo87();
        Future<String> future = demo.recognizeImage("/tmp/images/0001.png");
        // 执行其他操作
        doSomething();
        try {
            // 仅在需要相应任务的处理结果时才调用Future.get()
            Debug.info(future.get());
        } catch (InterruptedException e) {
            // 什么也不做
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static void doSomething() {
        Tools.randomPause(200);
    }

    public Future<String> recognizeImage(final String imageFile) {
        return executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return doRecognizeImage(new File(imageFile));
            }
        });
    }

    protected String doRecognizeImage(File imageFile) {
        String result = null;
        // 模拟实际运行结果
        String[] simulatedResults = { "苏Z MM518", "苏Z XYZ618", "苏Z 007618" };
        result = simulatedResults[(int) (Math.random() * simulatedResults.length)];
        Tools.randomPause(100);
        // 省略其他代码
        return result;
    }

}
