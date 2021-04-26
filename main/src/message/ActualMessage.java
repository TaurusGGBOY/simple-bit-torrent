package message;

import java.util.Arrays;

public class ActualMessage extends Message {
    private int len;
    private int type;
    private byte[] payload;

    public static final int CHOKE = 0;
    public static final int UNCHOKE = 1;
    public static final int INTERESTED = 2;
    public static final int NOTINTERESTED = 3;
    public static final int HAVE = 4;
    public static final int BITFIELD = 5;
    public static final int REQUEST = 6;
    public static final int PIECE = 7;

    public ActualMessage(int type, String peerID) {
        this.type = type;
        this.sendTo = peerID;
        this.payload = new byte[0];
    }

    public ActualMessage(byte[] bytes) {
        String str = new String(bytes);
        len = Integer.parseInt(str.substring(0, 4));
        type = Integer.parseInt(str.substring(4, 5));
        try {
            payload = Arrays.copyOfRange(bytes, 5, bytes.length);
        } catch (Exception e) {
            payload = new byte[0];
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 4 - String.valueOf(len).length(); i++) {
            stringBuilder.append("0");
        }
        stringBuilder.append(String.valueOf(len)).append(String.valueOf(type)).append(new String(payload));
        return stringBuilder.toString();
    }

    public byte[] toBytes() {
        return toString().getBytes();
    }

    public void cacularAndSetLen() {
        len = 4 + 1 + (payload == null ? 0 : payload.length);
    }


    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }
}