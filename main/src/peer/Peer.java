package peer;

public class Peer {
    private String ID;
    private String hostName;
    private int port;
    private boolean hasFileOrNot;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isHasFileOrNot() {
        return hasFileOrNot;
    }

    public void setHasFileOrNot(boolean hasFileOrNot) {
        this.hasFileOrNot = hasFileOrNot;
    }
}