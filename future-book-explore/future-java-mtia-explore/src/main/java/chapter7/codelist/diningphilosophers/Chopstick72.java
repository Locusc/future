package chapter7.codelist.diningphilosophers;

public class Chopstick72 {

    public final int id;
    private Status status = Status.PUT_DOWN;

    public static enum Status {
        PICKED_UP,
        PUT_DOWN
    }

    public Chopstick72(int id) {
        super();
        this.id = id;
    }

    public void pickUp() {
        status = Status.PICKED_UP;
    }

    public void putDown() {
        status = Status.PUT_DOWN;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "chopstick-" + id;
    }

}
