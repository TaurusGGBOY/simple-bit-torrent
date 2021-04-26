package message;

public class ShakeHandMessage extends Message {
    public static final String header = "P2PFILESHARINGPROJ";
    public static final String zeroBits = "0000000000";

    private String from;
    private String to;

    public ShakeHandMessage() {
    }

    public ShakeHandMessage(byte[] bytes) {
        String str = new String(bytes);
        from = str.substring(28,32);
    }

    @Override
    public String toString() {
        return header + zeroBits + String.valueOf(from);
    }

    public byte[] toBytes() {
        return toString().getBytes();
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}