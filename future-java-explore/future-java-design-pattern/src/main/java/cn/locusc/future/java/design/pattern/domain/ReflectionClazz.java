package cn.locusc.future.java.design.pattern.domain;

public class ReflectionClazz {

    private String message;

    public ReflectionClazz(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Export{" +
                "message='" + message + '\'' +
                '}';
    }

}
