package chapter2.codelist;

public class SafeCircularSeqGenerator implements CircularSeqGenerator21 {

    private short sequence = -1;

    @Override
    public synchronized short nextSequence() {
        if (sequence >= 999) {
            sequence = 0;
        } else {
            sequence++;
        }
        return sequence;
    }

}
