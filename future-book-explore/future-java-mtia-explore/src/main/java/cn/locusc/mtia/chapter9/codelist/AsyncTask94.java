package cn.locusc.mtia.chapter9.codelist;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jay
 * 9.3.2可重复执行的异步任务
 *
 * FutureTask基本上是被设计用来表示一次性执行的任务，其内部会维护一个表示任务运行状态（包括未开始运行、已经运行结束等)的状态变量，
 * FutureTask.run()在执行任务处理逻辑前会先判断相应任务的运行状态,如果该任务已经被执行过,
 * 那么FutureTask.run()会直接返回（并不会抛出异常)。因此，FutureTask 实例所代表的任务是无法被重复执行的。
 * 这意味着同一个FutureTask实例不能多次提交给Executor实例执行(尽管这样做不会导致异常的抛出 )。
 * FutureTask.runAndReset()能够打破这种限制，使得一个FutureTask 实例所代表的任务能够多次被执行。
 * FutureTask.runAndReset()是一个protected方法，它能够执行FutureTask实例所代表的任务但是不记录任务的处理结果。
 * 因此，如果同一个对象所表示的任务需要被多次执行，并且我们需要对该任务每次的执行结果进行处理，那么FutureTask 仍然是不适用的，
 * 此时我们可以考虑使用如清单9-4所示的抽象异步任务类AsyncTask来表示这种任务。
 *
 * AsyncTask抽象类同时实现了Runnable接口和Callable接口。AsyncTask子类通过覆盖call方法来实现其任务处理逻辑，
 * 而 AsyncTask.run()则充当任务处理逻辑的执行入口。AsyncTask 实例可以提交给Executor实例执行。当任务执行成功结束后，
 * 相应 AsyncTask实例的onResult方法会被调用以处理任务的执行结果;当任务执行过程中抛出异常时,
 * 相应AsyncTask实例的 onError方法会被调用以处理这个异常。AsyncTask的子类可以覆盖onResult方法、onError方法来对任务执行结果、
 * 任务执行过程中抛出的异常进行处理。
 * 由于AsyncTask 在回调onResult、onError方法的时候不是直接调用而是通过向 Executor实例 executor提交一个任务进行的,
 * 因此 AsyncTask 的任务执行(即 AsyncTask.run()调用)可以是在一个工作者线程中进行的,而对任务执行结果的处理则可以在另外一个线程中进行，
 * 这就从整体上实现了任务的执行与对任务执行结果的处理的并发:设asyncTask 为一个任意AsyncTask实例,
 * 当一个线程在执行asyncTask.onResult方法处理asyncTask一次执行的执行结果时，另外一个工作者线程可能正在执行asyncTask.run()，
 * 即进行asyncTask的下一次执行。
 *
 * 注意
 * FutureTask所代表的任务无法被多次执行，除非相应的任务是通过调用FutureTask.runAndReset()方法执行的。
 *
 * 2022/7/21
 */
public abstract class AsyncTask94<V> implements Runnable, Callable<V> {

    final static Logger LOGGER = Logger.getAnonymousLogger();

    protected final Executor executor;

    public AsyncTask94(Executor executor) {
        this.executor = executor;
    }

    public AsyncTask94() {
        this(new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        });
    }

    @Override
    public void run() {
        Exception exp = null;
        V r = null;
        try {
            r = call();
        } catch (Exception e) {
            exp = e;
        }

        final V result = r;
        if (null == exp) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    onResult(result);
                }
            });
        } else {
            final Exception exceptionCaught = exp;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    onError(exceptionCaught);
                }
            });
        }
    }

    /**
     * 留给子类实现任务执行结果的处理逻辑。
     *
     * @param result
     *          任务执行结果
     */
    protected abstract void onResult(V result);

    /**
     * 子类可覆盖该方法来对任务执行过程中抛出的异常进行处理。
     *
     * @param e 任务执行过程中抛出的异常
     */
    protected void onError(Exception e) {
        LOGGER.log(Level.SEVERE, "AsyncTask[" + this + "] failed.", e);
    }

}
