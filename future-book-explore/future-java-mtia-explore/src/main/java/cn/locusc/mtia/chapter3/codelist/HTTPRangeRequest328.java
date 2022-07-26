package cn.locusc.mtia.chapter3.codelist;

/**
 * @author Jay
 * final 关键字保障对象初始化完毕示例
 *
 * 我们知道,在JIT编译器的内联（ Inline）优化的作用下，如下语句
 * instance m new HTTPRangeRequest ("http://xyz.com/download/big.tar",0,1048576);
 * 可能会被编译成与如下伪代码等效的指令:
 * objRef = allocate (HTTPRangeRequest.class);//子操作①:分配对象所需的存储空间
 * objRef.url = "http://xyz.com/download/big.tar";
 * objRange = allocate(Range.class);
 * objRange.lowerBound = 0;//子操作②:初始化对象objRange
 * objRange.upperBound = 1048576;//子操作③:初始化对象objRange
 * objRef.range = objRange;//子操作④:发布对象objRange
 * instance = objRef; //子操作⑤:发布对象 objRef
 *
 * 由于实例变量range(引用型变量)采用final关键字修饰，
 * 因此Java语言会保障构造器中对该变量的初始化（赋值）操作（子操作④)以及该变量值所引用的对象(Range实例）的初始化（子操作②和子操作③）被限定在子操作⑤前完成。
 * 这就保障了HTTPRangeRequest实例对外可见的时候，该实例的 range字段所引用的对象已经初始化完毕。
 * 而url字段由于没有采用final修饰，因此Java虚拟机仍然可能将其重排序到子操作⑤之后。
 *
 *
 * 这里需要注意,final关键字只能保障有序性，即保障一个对象对外可见的时候该对象的 final字段必然是初始化完毕的。final 关键字并不保障对象引用本身对外的可见性。
 * 注意: 当一个对象的引用对其他线程可见的时候,这些线程所看到的该对象的final字段必然是初始化完毕的。
 * final关键字的作用仅是这种有序性的保障，它并不能保障包含final字段的对象的引用自身对其他线程的可见性。
 *
 * 2022/7/12
 */
public class HTTPRangeRequest328 {

    private final Range range;

    private String url;

    public HTTPRangeRequest328(String url, int lowerBound, int upperBound) {
        this.url = url;
        this.range = new Range(lowerBound, upperBound);
    }

    public static class Range {

        private long lowerBound;
        private long upperBound;

        public Range(long lowerBound, long upperBound) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }

        public long getLowerBound() {
            return lowerBound;
        }

        public void setLowerBound(long lowerBound) {
            this.lowerBound = lowerBound;
        }

        public long getUpperBound() {
            return upperBound;
        }

        public void setUpperBound(long upperBound) {
            this.upperBound = upperBound;
        }

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Range getRange() {
        return range;
    }

}
