package cn.locusc.reactive.other.sse.entity;

public class LoanProcesses {

    private final String messages;

    public LoanProcesses(String messages) {
        this.messages = messages;
    }

    public String getValue() {
        return messages;
    }

}
