package chapter4.codelist.case02;

/**
 * @author Jay
 * 日志记录集。 包含若干条日志记录。
 * 2022/7/13
 */
public class RecordSet48 {
    public final int capacity;
    final String[] records;
    int readIndex = 0;
    int writeIndex = 0;

    public RecordSet48(int capacity) {
        this.capacity = capacity;
        records = new String[capacity];
    }

    public String nextRecord() {
        String record = null;
        if (readIndex < writeIndex) {
            record = records[readIndex++];
        }
        return record;
    }

    public boolean putRecord(String line) {
        if (writeIndex == capacity) {
            return true;
        }
        records[writeIndex++] = line;
        return false;
    }

    public void reset() {
        readIndex = 0;
        writeIndex = 0;
        for (int i = 0, len = records.length; i < len; i++) {
            records[i] = null;
        }
    }

    public boolean isEmpty() {
        return 0 == writeIndex;
    }
}
