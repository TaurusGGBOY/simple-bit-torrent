package message;

public class ShakeHandMessage {
    private final String header = "P2PFILESHARINGPROJ";
    private final String zeroBits = "0000000000";
    private String peerID;

    public ShakeHandMessage() {
    }

    public ShakeHandMessage(byte[] bytes) {
        String str = new String(bytes);
        peerID = str.substring(bytes.length - 4);
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