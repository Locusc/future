package cn.locusc.mtia.chapter9.codelist;

import org.w3c.dom.Document;
import cn.locusc.mtia.utils.Debug;
import cn.locusc.mtia.utils.Tools;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.*;

/**
 * @author Jay
 * 基于 FutureTask的XML异步解析器
 *
 * 无论是 Runnable实例还是Callable实例所表示的任务，只要我们将其提交给线程池执行，那么这些任务就是异步任务。
 * 采用 Runnable实例来表示异步任务，其优点是任务既可以交给一个专门的工作者线程执行（以相应的Runnable实例为参数创建并启动一个工作者线程)，
 * 也可以交给一个线程池或者Executor 的其他实现类来执行;其缺点是我们无法直接获取任务的执行结果。
 * 使用Callable实例来表示异步任务，其优点是我们可以通过ThreadPoolExecutor.submit(Callable<T>)的返回值获取任务的处理结果﹔
 * 其缺点是Callable实例表示的异步任务只能交给线程池执行，而无法直接交给一个专门的工作者线程或者Executor实现类执行。
 * 因此，使用Callable实例来表示异步任务会使任务执行方式的灵活性大为受限。
 *
 * java.util.concurrent.FutureTask类则融合了 Runnable接口和 Callable接口的优点:
 * FutureTask是 Runnable接口的一个实现类,因此FutureTask表示的异步任务可以交给专门的工作者线程执行，
 * 也可以交给Executor实例(比如线程池）执行;FutureTask还能够直接返回其代表的异步任务的处理结果。
 * ThreadPoolExecutor.submit(Callable<T> task)的返回值就是一个FutureTask实例。
 * FutureTask 是java.util.concurrent.RunnableFuture接口的一个实现类。
 * 由于RunnableFuture接口继承了Future接口和Runnable接口，因此 FutureTask既是 Runnable接口的实现类也是 Future接口的实现。
 * FutureTask的一个构造器可以将Callable实例转换为Runnable实例,该构造器的声明如下:
 * public FutureTask (Callable<v> callable)
 *
 * 该构造器使得我们能够方便地创建一个能够返回处理结果的异步任务。我们可以将任务的处理逻辑封装在一个Callable实例中，
 * 并以该实例为参数创建一个 FutureTask实例。由于FutureTask类实现了Runnable接口，
 * 因此上述构造器的作用就相当于将Callable实例转换为 Runnable实例，而 FutureTask实例本身也代表了我们要执行的任务。
 * 我们可以用FutureTask实例( Runnable实例)为参数来创建并启动一个工作者线程以执行相应的任务，
 * 也可以将FutureTask 实例交给Executor执行（通过Executor.execute(Runnable task)调用)。
 * FuturcTask类还实现了Future接口,
 * 这使得我们在调用Exccutor.cxccute(Runnable task)这样只认 Runnable接口的方法来执行任务的情况下依然能够获取任务的执行结果:
 * 一个工作者线程(可以是线程池中的一个工作者线程)负责调用FutureTask.run()执行相应的任务，
 * 另外一个线程则调用FutureTask.get()来获取任务的执行结果。因此，FutureTask实例可被看作一个异步任务，
 * 它使得任务的执行和对任务执行结果的处理得以并发执行，从而有利于提高系统的并发性。
 *
 * ThreadPoolExecutor.submit(Callable<T> task)方法继承自 AbstractExecutorService.submit(Callable<T> task)。
 * AbstractExecutorService.submit(Callable<T> task)内部实现就是借助FutureTask 的，如图9-2所示。
 * submit方法会根据指定的Callable实例task 创建一个FutureTask实例ftask ,并通过Executor.execute(Runnable)调用异步执行ftask所代表的任务，
 * 然后返回ftask，以便该方法的调用方能够获取任务的执行结果。
 *
 * public<T> Future<T>submit(Callable<T> task) {
 *      if (task == null) throw new NullPointerException();
 *      RunnableFuture<T>ftask = newTaskFor(task);
 *      execute(ftask);
 *      return ftask;
 * }
 * 图9-2AbstractExecutorService.submit(Callable<T> )源码
 *
 * FutureTask还支持以回调（ Callback )的方式处理任务的执行结果。当FutureTask实例所代表的任务执行结束后，
 * FutureTask.done()会被执行5。FutureTask.done()是个protected方法，FutureTask子类可以覆盖该方法并在其中实现对任务执行结果的处理。
 * FutureTask.done()中的代码可以通过FutureTask.get()调用来获取任务的执行结果，此时由于任务已经执行结束，
 * 因此FutureTask.get()调用并不会使得当前线程暂停。但是，由于任务的执行结束既包括正常终止，也包括异常终止以及任务被取消而导致的终止，
 * 因此FutureTask.done()方法中的代码可能需要在调用FutureTask.get()前调用FutureTask.isCancelled()来判断任务是否被取消，
 * 以免 FutureTask.get()调用抛出CancellationException异常（运行时异常)，如清单9-3所示。
 *
 * 9.3.1 实践:实现XML文档的异步解析
 * Java标准库所提供的XML文档解析器javax.xml.parsers.DocumentBuilder仅支持以同步的方式去解析XML文档,
 * 这意味着直接使用DocumentBuilder解析XML文档，我们必须等待XML 文档解析完毕才能从XML 文档中查询数据。
 * 利用FutureTask我们可以自行实现一个支持异步解析的XML解析器XMLDocumentParser，如清单9-3所示。
 *
 * 2022/7/21
 */
public class XMLDocumentParser93 {

    public static ParsingTask newTask(InputStream in) {
        return new ParsingTask(in);
    }

    public static ParsingTask newTask(URL url) throws IOException {
        return newTask(url, 30000, 30000);
    }

    public static ParsingTask newTask(String strURL) throws IOException {
        URL url = new URL(strURL);
        return newTask(url);
    }

    public static ParsingTask newTask(URL url, int connectTimeout, int readTimeout)
            throws IOException {
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
        InputStream in = conn.getInputStream();
        return newTask(in);
    }

    public static ParsingTask newTask(String strURL, int connectTimeout, int readTimeout)
            throws IOException {
        URL url = new URL(strURL);
        return newTask(url, connectTimeout, readTimeout);
    }

    // 封装对XML解析结果进行处理的回调方法
    public abstract static class ResultHandler {
        abstract void onSuccess(Document document);

        void onError(Throwable e) {
            e.printStackTrace();
        }
    }

    public static class ParsingTask {
        private final InputStream in;
        private volatile Executor executor;
        private volatile ResultHandler resultHandler;

        public ParsingTask(InputStream in, Executor executor, ResultHandler resultHandler) {
            this.in = in;
            this.executor = executor;
            this.resultHandler = resultHandler;
        }

        public ParsingTask(InputStream in) {
            this(in, null, null);
        }

        public Future<Document> execute() throws Exception {
            FutureTask<Document> ft;

            final Callable<Document> task = new Callable<Document>() {
                @Override
                public Document call() throws Exception {
                    return doParse(in);
                }
            };
            final Executor theExecutor = executor;
            // 解析模式：异步/同步
            final boolean isAsyncParsing = null != theExecutor;
            final ResultHandler rh;
            if (isAsyncParsing && null != (rh = resultHandler)) {
                ft = new FutureTask<Document>(task) {
                    @Override
                    protected void done() {
                        // 回调ResultHandler的相关方法对XML解析结果进行处理
                        callbackResultHandler(this, rh);
                    }
                };// FutureTask匿名类结束
            } else {
                ft = new FutureTask<Document>(task);
            }

            if (isAsyncParsing) {
                theExecutor.execute(ft);// 交给Executor执行，以支持异步执行
            } else {
                ft.run();// 直接（同步）执行
            }
            return ft;
        }

        void callbackResultHandler(FutureTask<Document> ft, ResultHandler rh) {
            // 获取任务处理结果前判断任务是否被取消
            if (ft.isCancelled()) {
                Debug.info("parsing cancelled.%s", ParsingTask.this);
                return;
            }
            try {
                Document doc = ft.get();
                rh.onSuccess(doc);
            } catch (InterruptedException ignored) {
                Debug.info("retrieving result cancelled.%s", ParsingTask.this);
            } catch (ExecutionException e) {
                rh.onError(e.getCause());
            }
        }

        static Document doParse(InputStream in) throws Exception {
            Document document = null;
            try {
                DocumentBuilder db = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder();
                document = db.parse(in);
            } finally {
                Tools.silentClose(in);
            }
            return document;
        }

        public ParsingTask setExecutor(Executor executor) {
            this.executor = executor;
            return this;
        }

        public ParsingTask setResultHandler(ResultHandler resultHandler) {
            this.resultHandler = resultHandler;
            return this;
        }
    }// ParsingTask定义结束

}
