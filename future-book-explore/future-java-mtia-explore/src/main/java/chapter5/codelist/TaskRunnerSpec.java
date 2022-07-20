package chapter5.codelist;

public interface TaskRunnerSpec {

    public void init();

    public void submit(Runnable task) throws InterruptedException;

}
