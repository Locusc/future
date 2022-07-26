package cn.locusc.mtia.chapter9.codelist;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import cn.locusc.mtia.utils.Debug;
import cn.locusc.mtia.utils.Tools;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author Jay
 * 使用 ExecutorCompletionService 实现文件异步批量上传
 *
 * 9.2.2 异步任务的批量执行:CompletionService
 * 尽管Future接口使得我们能够方便地获取异步任务的处理结果,但是如果需要一次性提交一批异步任务并获取这些任务的处理结果的话,
 * 那么仅使用Future接口写出来的代码将颇为烦琐。java.util.concurrent.CompletionService接口为异步任务的批量提交以及获取这些任务的处理结果提供了便利。
 *
 * CompletionService接口定义的一个 submit方法可用于提交异步任务，该方法的签名与ThreadPoolExecutor的一个submit方法相同:
 * Future<v> submit (Callable<v> task)
 *
 * task参数代表待执行的异步任务，该方法的返回值可用于获取相应异步任务的处理结果。如果是批量提交异步任务，
 * 那么通常我们并不关心该方法的返回值。若要获取批量提交的异步任务的处理结果，那么我们可以使用CompletionService 接口专门为此定义的方法，其中的一个方法是:
 * Future<v> take() throws InterruptedException
 *
 * 该方法与BlockingQueue.take()相似，它是一个阻塞方法，其返回值是一个已经执行结束的异步任务对应的 Future实例,
 * 该实例就是提交相应任务时submit(Callable<V>)调用的返回值。如果 take()被调用时没有已执行结束的异步任务，
 * 那么take()的执行线程就会被暂停，直到有异步任务执行结束。因此，我们批量提交了多少个异步任务，
 * 则多少次连续调用CompletionService.take()便可以获取这些任务的处理结果。
 *
 * CompletionService也定义了两个非阻塞方法用于获取异步任务的处理结果:
 * Future<v> poll ()
 * Future<V> poll(long timeout,TimeUnit unit) throws InterruptedException
 *
 * 这两个方法与BlockingQueue的 poll方法相似，它们的返回值是已执行结束的异步任务对应的 Future实例。
 *
 * Java标准库提供的 CompletionService接口的实现类是 ExecutorCompletionService。ExecutorCompletionService的一个构造器是:
 * ExecutorCompletionservice (Executor executor,BlockingQueue<Future<v>> completionQueue)
 *
 * 由此可见，ExecutorCompletionService相当于Executor'实例与 BlockingQueue实例的一个融合体。
 * 其中，Executor实例负责接收并执行异步任务，而BlockingQueue实例则用于存储已执行完毕的异步任务对应的Future实例。
 * ExecutorCompletionService会为其客户端提交的每个异步任务( Callable实例或者Runnable实例)都创建一个相应的Future实例,
 * 通过该实例其客户端代码便可以获取相应异步任务的处理结果。ExecutorCompletionService每执行完一个异步任务,
 * 就将该任务对应的Future实例存入其内部维护的BlockingQueue实例之中,
 * 而其客户端代码则可以通过ExecutorCompletionService.take()调用来获取这个 Future实例。
 *
 * 使用ExecutorCompletionService的另外一个构造器ExecutorCompletionService(Executorexecutor)创建实例相当于:
 * new ExecutorCompletionService<v> (executor,new LinkedBlockingQueue<Future<v>> ());
 *
 * 3FTP服务器由构造器中的ftpServer参数指定。
 * 4这种文件中的内容为相应文件对应的MDS摘要值。这里，MD5摘要文件的作用一方面是供对方(即
 * 使用上传的文件的程序)进行数据完整性校验,另一方面它充当了原始任务中相应文件上传完毕的标记，
 * 即对方只有在“看到”一个MD5文件的情况下才能认为相应的原始任务文件的上传是结束的。
 *
 * 下面看一个实战案例，该案例中我们使用ExecutorCompletionService 以异步方式实现文件的批量FTP上传，如清单9-2所示。
 * FileBatchUploader.uploadFiles方法能够将指定的一批文件以异步方式上传到指定的FTP服务器3:
 * 该方法将这批文件的上传视作一个任务(以下称之为原始任务)并创建一个相应的Runnable实例将其提交给dispatcher ( Executor实例)执行。
 * 原始任务的执行是通过调用doUploadFiles方法实现的。在doUploadFiles方法中,我们为原始任务中的每个文件都创建一个相应的文件上传任务(UploadTask实例),
 * 并将这些任务批量提交给completionService ( CompletionService实例)执行。然后，对于原始任务中的每个文件，
 * 一旦一个文件上传结束，即 completionService.take()调用返回，那么我们就将该文件移动到备份目录并为该文件生成相应的MD5摘要文件。
 * 接着,我们为每个MD5摘要文件创建一个相应的文件上传任务，并将其提交给completionService执行。这里，对于原始任务中的每个文件，
 * 文件的实际上传是在一个线程(即Executor实例es 中维护的一个单工作者线程)中执行的，
 * 而在该文件上传完毕后将其移动到备份目录以及生成相应的MD5文件这些操作则是在另外一个线程（即 Executor 实例 dispatcher 中维护的一个单工作者线程)中执行的,
 * 即文件的上传与对上传完毕文件的后续处理是并发的。这种并发得以实现正是得益于CompletionService 所支持的批量异步任务提交以及获取执行任务对应的 Future 实例。
 *
 * ExecutorService.invokeAll(Collection<? extends Callable<T>> tasks)也能够用来批量提交异步任务，
 * 该方法能够并发执行tasks参数所指定的一批任务，但是该方法只有在 tasks参数所指定的一批任务中的所有任务都执行结束之后才返回,
 * 其返回值是一个包含各个任务对应的 Future实例的列表(List )。
 * 因此，使用invokeAll方法提交批量任务的时候，任务提交方等待invokeAll方法返回的时间取决于这批任务中最耗时的任务的执行耗时。
 *
 * 2022/7/21
 */
public class FileBatchUploader92 implements Closeable {

    private final String ftpServer;
    private final String userName;
    private final String password;
    private final String targetRemoteDir;
    private final FTPClient ftp = new FTPClient();
    private final CompletionService<File> completionService;
    private final ExecutorService es;
    private final ExecutorService dispatcher;

    public FileBatchUploader92(String ftpServer, String userName, String password,
                             String targetRemoteDir) {
        this.ftpServer = ftpServer;
        this.userName = userName;
        this.password = password;
        this.targetRemoteDir = targetRemoteDir;
        // 使用单工作者线程的线程池
        this.es = Executors.newSingleThreadExecutor();
        this.dispatcher = Executors.newSingleThreadExecutor();
        this.completionService = new ExecutorCompletionService<>(es);
    }

    public void uploadFiles(final Set<File> files) {
        dispatcher.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    doUploadFiles(files);
                } catch (InterruptedException ignored) {
                }
            }
        });
    }

    private void doUploadFiles(Set<File> files) throws InterruptedException {
        // 批量提交文件上传任务
        for (final File file : files) {
            completionService.submit(new UploadTask(file));
        }

        Future<File> future;
        File md5File;
        File uploadedFile;
        Set<File> md5Files = new HashSet<File>();
        for (File file : files) {
            try {
                future = completionService.take();
                uploadedFile = future.get();
                // 将上传成功的文件移动到备份目录，并为其生成相应的MD5文件
                md5File = generateMD5(moveToSuccessDir(uploadedFile));
                md5Files.add(md5File);
            } catch (ExecutionException | IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
                moveToDeadDir(file);
            }
        }
        for (File file : md5Files) {
            // 上传相应的MD5文件
            completionService.submit(new UploadTask(file));
        }
        // 检查md5文件的上传结果
        int successUploaded = md5Files.size();
        for (int i = 0; i < successUploaded; i++) {
            future = completionService.take();
            try {
                uploadedFile = future.get();
                md5Files.remove(uploadedFile);
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        // 将剩余（即未上传成功）的md5文件移动到相应备份目录
        for (File file : md5Files) {
            moveToDeadDir(file);
        }
    }

    private File generateMD5(File file) throws IOException, NoSuchAlgorithmException {
        String md5 = Tools.md5sum(file);
        File md5File = new File(file.getAbsolutePath() + ".md5");
        Files.write(Paths.get(md5File.getAbsolutePath()), md5.getBytes("UTF-8"));
        return md5File;
    }

    private static File moveToSuccessDir(File file) {
        File targetFile = null;
        try {
            targetFile = moveFile(file, Paths.get(file.getParent(), "..", "backup", "success"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return targetFile;
    }

    private static File moveToDeadDir(File file) {
        File targetFile = null;
        try {
            targetFile = moveFile(file, Paths.get(file.getParent(), "..", "backup", "dead"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return targetFile;
    }

    private static File moveFile(File srcFile, Path destPath) throws IOException {
        Path sourcePath = Paths.get(srcFile.getAbsolutePath());
        if (!Files.exists(destPath)) {
            Files.createDirectories(destPath);
        }
        Path destFile = destPath.resolve(srcFile.getName());
        Files.move(sourcePath, destFile,
                StandardCopyOption.REPLACE_EXISTING);
        return destFile.toFile();
    }

    class UploadTask implements Callable<File> {
        private final File file;

        public UploadTask(File file) {
            this.file = file;
        }

        @Override
        public File call() throws Exception {
            Debug.info("uploading %s", file.getCanonicalPath());
            // 上传指定的文件
            upload(file);
            return file;
        }
    }

    // 初始化FTP客户端
    public void init() throws Exception {
        FTPClientConfig config = new FTPClientConfig();
        ftp.configure(config);
        int reply;
        ftp.connect(ftpServer);
        Debug.info("FTP Reply:%s", ftp.getReplyString());
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new Exception("FTP server refused connection.");
        }
        boolean isOK = ftp.login(userName, password);
        if (isOK) {
            Debug.info("FTP Reply:%s", ftp.getReplyString());
        } else {
            throw new Exception("Failed to login." + ftp.getReplyString());
        }
        reply = ftp.cwd(targetRemoteDir);
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new Exception("Failed to change working directory.reply:"
                    + reply);
        } else {
            Debug.info("FTP Reply:%s", ftp.getReplyString());
        }
        ftp.setFileType(FTP.ASCII_FILE_TYPE);
    }

    // 将指定的文件上传至FTP服务器
    protected void upload(File file) throws Exception {
        boolean isOK;
        try (InputStream dataIn = new BufferedInputStream(new FileInputStream(file))) {
            isOK = ftp.storeFile(file.getName(), dataIn);
        }
        if (!isOK) {
            throw new IOException("Failed to upload " + file + ",reply:" + ","
                    + ftp.getReplyString());
        }
    }

    @Override
    public void close() throws IOException {
        dispatcher.shutdown();
        try {
            es.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }
        es.shutdown();
        try {
            es.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }
        Tools.silentClose(new Closeable() {
            @Override
            public void close() throws IOException {
                if (ftp.isConnected()) {
                    ftp.disconnect();
                }
            }
        });
    }

}
