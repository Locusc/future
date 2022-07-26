package cn.locusc.mtia.chapter4.codelist.case01;

import cn.locusc.mtia.utils.Debug;
import cn.locusc.mtia.utils.Tools;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Jay
 * 大文件下载器入口类源码
 * 2022/7/13
 */
public class BigFileDownloader41 {

    protected final URL requestURL;

    protected final long fileSize;

    /**
     * 负责已下载数据的存储
     */
    protected final Storage44 storage44;

    protected final AtomicBoolean taskCanceled = new AtomicBoolean(false);

    public BigFileDownloader41(String strURL) throws Exception {
        requestURL = new URL(strURL);

        // 获取待下载资源的大小（单位：字节）
        fileSize = retieveFileSize(requestURL);
        Debug.info("file total size:%s", fileSize);
        String fileName = strURL.substring(strURL.lastIndexOf('/') + 1);
        // 创建负责存储已下载数据的对象
        storage44 = new Storage44(fileSize, fileName);
    }

    /**
     * 下载指定的文件
     *
     * @param taskCount
     *          任务个数
     * @param reportInterval
     *          下载进度报告周期
     * @throws Exception
     */
    public void download(int taskCount, long reportInterval)
            throws Exception {

        long chunkSizePerThread = fileSize / taskCount;
        // 下载数据段的起始字节
        long lowerBound = 0;
        // 下载数据段的结束字节
        long upperBound = 0;

        DownloadTask42 dt;
        for (int i = taskCount - 1; i >= 0; i--) {
            lowerBound = i * chunkSizePerThread;
            if (i == taskCount - 1) {
                upperBound = fileSize;
            } else {
                upperBound = lowerBound + chunkSizePerThread - 1;
            }

            // 创建下载任务
            dt = new DownloadTask42(lowerBound, upperBound, requestURL, storage44,
                    taskCanceled);
            dispatchWork(dt, i);
        }
        // 定时报告下载进度
        reportProgress(reportInterval);
        // 清理程序占用的资源
        doCleanup();

    }

    protected void doCleanup() {
        Tools.silentClose(storage44);
    }

    protected void cancelDownload() {
        if (taskCanceled.compareAndSet(false, true)) {
            doCleanup();
        }
    }

    protected void dispatchWork(final DownloadTask42 dt, int workerIndex) {
        // 创建下载线程
        Thread workerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dt.run();
                } catch (Exception e) {
                    e.printStackTrace();
                    // 取消整个文件的下载
                    cancelDownload();
                }
            }
        });
        workerThread.setName("downloader-" + workerIndex);
        workerThread.start();
    }

    // 根据指定的URL获取相应文件的大小
    private static long retieveFileSize(URL requestURL) throws Exception {
        long size = -1;
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) requestURL.openConnection();

            conn.setRequestMethod("HEAD");
            conn.setRequestProperty("Connection", "Keep-alive");
            conn.connect();
            int statusCode = conn.getResponseCode();
            if (HttpURLConnection.HTTP_OK != statusCode) {
                throw new Exception("Server exception,status code:" + statusCode);
            }

            String cl = conn.getHeaderField("Content-Length");
            size = Long.valueOf(cl);
        } finally {
            if (null != conn) {
                conn.disconnect();
            }
        }
        return size;
    }

    // 报告下载进度
    private void reportProgress(long reportInterval) throws InterruptedException {
        float lastCompletion;
        int completion = 0;
        while (!taskCanceled.get()) {
            lastCompletion = completion;
            completion = (int) (storage44.getTotalWrites() * 100 / fileSize);
            if (completion == 100) {
                break;
            } else if (completion - lastCompletion >= 1) {
                Debug.info("Completion:%s%%", completion);
                if (completion >= 90) {
                    reportInterval = 1000;
                }
            }
            Thread.sleep(reportInterval);
        }
        Debug.info("Completion:%s%%", completion);
    }

}
