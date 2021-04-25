package peer;

import java.util.HashSet;
import java.util.Set;

public class Peer {
    private String ID;
    private String hostName;
    private int port;
    private boolean hasFileOrNot;

    private int rate;
    private boolean choke;
    public Set<Integer> pieces;
    private boolean interstedInLocal;

    public Peer() {
        pieces = new HashSet<>();
        choke = true;
        interstedInLocal = false;
    }

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

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public void increaseRate() {
        rate++;
    }

    public void resetRate() {
        rate = 0;
    }

    public boolean isChoke() {
        return choke;
    }

    public void setChoke(boolean choke) {
        this.choke = choke;
    }

    public boolean isInterstedInLocal() {
        return interstedInLocal;
    }

    public void setInterstedInLocal(boolean interstedInLocal) {
        this.interstedInLocal = interstedInLocal;
    }
}