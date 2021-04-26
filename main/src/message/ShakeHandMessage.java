package message;

public class ShakeHandMessage extends Message {
    public static final String header = "P2PFILESHARINGPROJ";
    public static final String zeroBits = "0000000000";
    private String peerID;

    public ShakeHandMessage() {
    }

    public ShakeHandMessage(byte[] bytes) {
        String str = new String(bytes);
        peerID = str.substring(28,32);
    }

    @Override
    public String toString() {
        return header + zeroBits + String.valueOf(peerID);
    }

    public byte[] toBytes() {
        return toString().getBytes();
    }

    public String getPeerID() {
        return peerID;
    }

    public void setPeerID(String peerID) {
        this.peerID = peerID;
    }
}