package chapter2.codelist;

public class AtomicityExample25 {

    private HostInfo hostInfo;

    public void updateHostInfo(String ip, int port) {
        // 以下操作不是原子操作
        hostInfo.setIp(ip);
        hostInfo.setPort(port);
    }

    public void updateHostInfo2(String ip, int port) {
        // 原子操作
        HostInfo newHostInfo = new HostInfo(ip, port);
        hostInfo = newHostInfo;
    }

    public void connectToHost() {
        String ip = hostInfo.getIp();
        int port = hostInfo.getPort();
        connectToHost(ip, port);
    }

    private void connectToHost(String ip, int port) {
        // ...
    }

    public static class HostInfo {
        private String ip;
        private int port;

        public HostInfo(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }
}
