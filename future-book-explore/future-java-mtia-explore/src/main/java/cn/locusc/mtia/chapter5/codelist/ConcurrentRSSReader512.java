package cn.locusc.mtia.chapter5.codelist;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import cn.locusc.mtia.utils.Tools;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * @author Jay
 * 边下载边解析的 RSS 阅读器
 *
 * 在loadRSS方法中，我们创建了一个工作者线程 workerThread 专门负责下载指定的RSS文件。
 * loadRSS方法会创建并返回一个 PipedInputStream实例in。workerThread.run()会创建一个与 in 关联的 PipedOutputStream实例 pos，
 * 并将该实例作为参数传递给doDownload(String , OutputStream)。
 * doDownload(String ,OutputStream)会根据其参数中指定的输出流（即上述的 PipedOutputStream实例 pos)创建一个-WritableByteChannel实例writeChannel，
 * 并将下载的RSS 数据写入 writeChannel。这实际上实现了将下载的RSS数据写入pos。main线程则会从输人流 in中读取数据并进行XML解析，
 * 而in中的数据来自workerThread 的输出，这就实现了一个线程( workerThread )的输出直接作为另外一个线程( main )的输人。
 * 从并发的角度来看,这实际上是实现了RSS 数据的边下载( workerThread负责下载）和边解析（ main线程负责解析)。
 *
 *
 * 使用PipedOutputStream和 PipedInputStream 时需要注意以下几点。
 * PipedOutputStream和 PipedInputStream适合在两个线程间使用，即适用于单生产者—单消费者的情形。
 * 在 PipedOutputStream和 PipedInputStream所实现的生产者—消费者模式中，产品不是一个普通的对象而是字节形式的原始数据，
 * 因此在生产者线程不止一个或者消费者线程不止一个的情况下，我们往往需要保证产品序列（字节流）的顺序性，
 * 而这可能增加代码的复杂性和额外开销，比如为保证数据的顺序性而引入额外的锁所导致的开销。
 * 另外，PipedOutputStream和PipedInputStream不宜在单线程程序中使用，因为那样可能导致无限制的等待(死锁）。
 *
 * 输出异常的处理。如果生产者线程在其执行过程中出现了不可恢复的异常，那么消费者线程就会永远也无法读取到新的数据。
 * 但是，由于消费者线程和生产者线程不是同一个线程，因此生产者线程中出现了异常，消费者线程是无法直接侦测的，
 * 即无法像单线程程序那样通过try-catch捕获异常。所以，生产者线程出现异常时需要通过某种方式“知会”相应的消费者线程,
 * 否则消费者线程可能会无限制地等待新的数据。生产者线程通常可以通过关闭PipedOutputStream实例来实现这种“知会”。
 * 例如在上述例子中，生产者线程workerThread 在 catch块中提前关闭 PipedOutputStream 实例 pos，
 * 以“知会”生产者线程( main线程）其无法继续提供新的数据。
 *
 * 注意
 * PipedOutputStream和 PipedInputStream适合在单生产者—单消费者模式中使用。
 * 生产者线程发生异常而导致其无法继续提供新的数据时，生产者线程必须主动提前关闭相应的PipedOutputStream实例（调用PipedOutputStream.close())
 *
 * 2022/7/15
 */
public class ConcurrentRSSReader512 {

    public static void main(String[] args) throws Exception {
        final int argc = args.length;
        String url = argc > 0 ? args[0] : "http://lorem-rss.herokuapp.com/feed";

        // 从网络加载RSS数据
        InputStream in = loadRSS(url);
        // 从输入流中解析XML数据
        Document document = parseXML(in);

        // 读取XML中的数据
        Element eleRss = (Element) document.getFirstChild();
        Element eleChannel = (Element) eleRss.getElementsByTagName("channel").item(
                0);
        Node ndTtile = eleChannel.getElementsByTagName("title").item(0);
        String title = ndTtile.getFirstChild().getNodeValue();
        System.out.println(title);
        // 省略其他代码
    }

    private static Document parseXML(InputStream in)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder();
        Document document = db.parse(in);
        return document;
    }

    private static InputStream loadRSS(final String url) throws IOException {
        final PipedInputStream in = new PipedInputStream();
        // 以in为参数创建PipedOutputStream实例
        final PipedOutputStream pos = new PipedOutputStream(in);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    doDownload(url, pos);
                } catch (Exception e) {
                    // RSS数据下载过程中出现异常时，关闭相关输出流和输入流。
                    // 注意，此处我们不能像平常那样在finally块中关闭相关输出流
                    Tools.silentClose(pos, in);
                    e.printStackTrace();
                }
            }// run方法结束
        }, "rss-loader");
        return in;
    }

    static BufferedInputStream issueRequest(String url) throws Exception {
        URL requestURL = new URL(url);
        final HttpURLConnection conn = (HttpURLConnection) requestURL
                .openConnection();
        conn.setConnectTimeout(2000);
        conn.setReadTimeout(2000);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Connection", "close");
        conn.setDoInput(true);
        conn.connect();
        int statusCode = conn.getResponseCode();
        if (HttpURLConnection.HTTP_OK != statusCode) {
            conn.disconnect();
            throw new Exception("Server exception,status code:" + statusCode);
        }

        BufferedInputStream in = new BufferedInputStream(conn.getInputStream()) {
            // 覆盖BufferedInputStream的close方法，使得输入流被关闭的时候HTTP连接也随之被关闭
            @Override
            public void close() throws IOException {
                try {
                    super.close();
                } finally {
                    conn.disconnect();
                }
            }
        };
        return in;
    }

    static void doDownload(String url, OutputStream os) throws Exception {
        ReadableByteChannel readChannel = null;
        WritableByteChannel writeChannel = null;
        try {
            // 对指定的URL发起HTTP请求
            BufferedInputStream in = issueRequest(url);
            readChannel = Channels.newChannel(in);
            ByteBuffer buf = ByteBuffer.allocate(1024);
            writeChannel = Channels.newChannel(os);
            while (readChannel.read(buf) > 0) {
                buf.flip();
                writeChannel.write(buf);
                buf.clear();
            }
        } finally {
            Tools.silentClose(readChannel, writeChannel);
        }
    } // doDownload方法结束

}
