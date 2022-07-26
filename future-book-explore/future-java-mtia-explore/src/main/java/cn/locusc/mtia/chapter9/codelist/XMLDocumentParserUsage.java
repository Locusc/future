package cn.locusc.mtia.chapter9.codelist;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import cn.locusc.mtia.utils.Debug;
import cn.locusc.mtia.utils.Tools;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Jay
 * XMLDocumentParser不仅支持异步方式解析，还支持同步方式解析。利用XMLDocumentParser,
 * 每次解析意味着创建一个ParsingTask实例并执行该实例的execute()方法。在ParsingTask.execute()中，
 * 我们先创建一个Callable<Document>实例 task 来表示针对指定输人流(InputStream)进行的XML解析任务。
 * 接着，我们设定XML文档的解析模式:如果客户端代码为当前ParsingTask 实例关联了一个 Executor实例（即ParsingTask.setExecutor方法被执行过)，
 * 那么我们就将解析模式设置为异步解析，否则就将解析模式设置为同步解析。然后，我们以 task为参数创建相应的FutureTask 实例ft:
 * 在异步解析模式下﹐我们创建一个 FutureTask的匿名子类,
 * 并在该子类的done()中实现XML解析结果的回调（ Callback )处理——若解析成功则以解析结果（ org.w3c.dom.Document )为参数调
 * ResultHandler.onSuccess方法，若解析失败则调用ResultHandler.onError方法;在同步解析模式下，我们直接通过new创建一个 FutureTask 实例。
 * 接下来便是安排ft的执行:在异步解析模式下，我们会将ft交给指定的Executor 实例来执行;在同步解析模式下，我们直接调用ft.run()来执行XML解析任务。
 * 此后，ParsingTask.execute()直接返回ft。
 *
 * 由此可见，在不使用 ResultHandler 的情况下，异步解析方式和同步解析方式的客户端代码编写方式几乎是一样的:
 * 异步解析方式比同步方式多了一个ParsingTask.setExecutor方法调用;在异步解析方式下，客户端代码在ParsingTask.execute()调用与
 * Future.get()调用之间往往会执行其他操作，以减少因XML异步解析未完成而导致Future.get()调用造成等待的可能性。
 * 从上述分析可知，FutureTask 的使用既可以发挥异步编程的好处，又可以在一定程度上屏蔽同步编程与异步编程之间的差异，这简化了代码。
 *
 * 2022/7/21
 */
public class XMLDocumentParserUsage {

    static ExecutorService es = Executors.newSingleThreadExecutor();

    public static void main(String[] args) throws Exception {
        final int argc = args.length;
        URL url = argc > 0 ? new URL(args[0]) : XMLDocumentParserUsage.class.getClassLoader()
                .getResource("data/ch9/feed");

        syncParse(url);
        asyncParse1(url);
        asyncParse2(url);
        Tools.delayedAction("The ExecutorService will be shutdown", new Runnable() {
            @Override
            public void run() {
                es.shutdown();
            }
        }, 70);
    }

    private static void syncParse(URL url) throws Exception {
        Future<Document> future;
        future = XMLDocumentParser93.newTask(url).execute();
        process(future.get());// 直接获取解析结果进行处理
    }

    /**
     * 这里我们指定了一个ResultHandler 以回调的方式来处理XML解析结果。
     */
    private static void asyncParse1(URL url) throws Exception {
        XMLDocumentParser93.newTask(url).setExecutor(es).setResultHandler(
                new XMLDocumentParser93.ResultHandler() {
                    @Override
                    public void onSuccess(Document document) {
                        process(document);
                    }
                }).execute();

    }

    /**
     * 同样是异步解析，我们也可以不指定ResultHandler，而是在程序需要XML解析结果的时候自己通过Future.get()调用来获取:
     */
    private static void asyncParse2(URL url) throws Exception {

        Future<Document> future = XMLDocumentParser93.newTask(url).setExecutor(es).execute();
        doSomething();// 执行其他操作
        process(future.get());

    }

    private static void doSomething() {
        Tools.randomPause(2000);
    }

    private static void process(Document document) {
        Debug.info(queryTitle(document));
    }

    private static String queryTitle(Document document) {
        Element eleRss = (Element) document.getFirstChild();
        Element eleChannel = (Element) eleRss.getElementsByTagName("channel")
                .item(0);
        Node ndTtile = eleChannel.getElementsByTagName("title").item(0);
        String title = ndTtile.getFirstChild().getNodeValue();
        return title;
    }


}
