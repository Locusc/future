package cn.locusc.mtia.chapter9.codelist.func;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AsyncTaskFunction<V, R> implements Runnable, Callable<V> {

    protected final Executor executor;

    // 任务执行结果的处理逻辑
    protected BiConsumer<V, R> onResult;

    // 任务执行过程中抛出的异常进行处理。
    protected Consumer<Exception> onError;

    // 任务执行过程中抛出的异常进行处理。
    protected Supplier<V> onCall;

    private R action;

    public AsyncTaskFunction(Executor executor) {
        this.executor = executor;
    }

    public AsyncTaskFunction() {
        this(Runnable::run);
    }

    @Override
    public void run() {
        Exception exp = null;
        V r = null;
        try {
            r = this.call();
        } catch (Exception e) {
            exp = e;
        }

        final V result = r;
        if (null == exp) {
            executor.execute(() -> onResult.accept(result, action));
        } else {
            final Exception exceptionCaught = exp;
            executor.execute(() -> onError.accept(exceptionCaught));
        }
    }

    @Override
    public V call() throws Exception {
        return onCall.get();
    }

    public AsyncTaskFunction<V, R> setAction(R action) {
        this.action = action;
        return this;
    }

    public AsyncTaskFunction<V, R> setOnCall(Supplier<V> onCall) {
        this.onCall = onCall;
        return this;
    }

    public AsyncTaskFunction<V, R> setOnResult(BiConsumer<V, R> onResult) {
        this.onResult = onResult;
        return this;
    }

    public AsyncTaskFunction<V, R> setOnError(Consumer<Exception> onError) {
        this.onError = onError;
        return this;
    }

}
