package cn.locusc.mtia.chapter4.codelist.case01;

import cn.locusc.mtia.utils.Debug;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author Jay
 * 当缓存区满时再将字节写入文件, 从而减少IO操作
 * 2022/7/13
 */
public class DownloadBuffer43 implements Closeable {

    /**
     * 当前Buffer中缓冲的数据相对于整个存储文件的位置偏移
     */
    private long globalOffset;
    private long upperBound;
    private int offset = 0;
    public final ByteBuffer byteBuf;
    private final Storage44 storage44;

    public DownloadBuffer43(long globalOffset, long upperBound,
                            final Storage44 storage44) {
        this.globalOffset = globalOffset;
        this.upperBound = upperBound;
        this.byteBuf = ByteBuffer.allocate(1024 * 1024);
        this.storage44 = storage44;
    }

    public void write(ByteBuffer buf) throws IOException {
        int length = buf.position();
        final int capacity = byteBuf.capacity();
        // 当前缓冲区已满，或者剩余容量不够容纳新数据
        if (offset + length > capacity || length == capacity) {
            // 将缓冲区中的数据写入文件
            flush();
        }
        byteBuf.position(offset);
        buf.flip();
        byteBuf.put(buf);
        offset += length;
    }

    public void flush() throws IOException {
        int length;
        byteBuf.flip();
        length = storage44.store(globalOffset, byteBuf);
        byteBuf.clear();
        globalOffset += length;
        offset = 0;
    }

    @Override
    public void close() throws IOException {
        Debug.info("globalOffset:%s,upperBound:%s", globalOffset, upperBound);
        if (globalOffset < upperBound) {
            flush();
        }
    }

}
