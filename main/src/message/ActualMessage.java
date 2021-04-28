package message;

import util.ByteUtil;

import java.util.Arrays;

public class ActualMessage extends Message {
    private int len;
    private int type;
    private byte[] payload;

    private String from;
    private String to;

    public static final int CHOKE = 0;
    public static final int UNCHOKE = 1;
    public static final int INTERESTED = 2;
    public static final int NOTINTERESTED = 3;
    public static final int HAVE = 4;
    public static final int BITFIELD = 5;
    public static final int REQUEST = 6;
    public static final int PIECE = 7;

    /**
     * 构造ActualMessage的时候用
     * @param type
     * @param from
     * @param to
     */
    public ActualMessage(int type, String from, String to) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.payload = new byte[0];
    }

    /**
     * 重建ActualMessage的时候用
     * @param bytes
     */
    public ActualMessage(byte[] bytes) {
        len = ByteUtil.byteArrayToInt(Arrays.copyOfRange(bytes, 0, 4));
        type = (int) bytes[4];
        try {
            payload = Arrays.copyOfRange(bytes, 5, 5 + len - 1);
        } catch (Exception e) {
            payload = new byte[0];
        }
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 4 - String.valueOf(len).length(); i++) {
            stringBuilder.append("0");
        }
        stringBuilder.append(String.valueOf(len)).append(String.valueOf(type)).append(new String(payload));
        return stringBuilder.toString();
    }

    /**
     * 返回字节流，不包括messageLength
     * @return
     */
    public byte[] toBytes() {
        byte[] res = new byte[4 + 1 + (payload == null ? 0 : payload.length)];
        byte[] lenBytes = ByteUtil.intToByteArray(len);

        System.arraycopy(lenBytes, 0, res, 0, lenBytes.length);
        res[4] = (byte) type;
        System.arraycopy(payload, 0, res, lenBytes.length + 1, len - 1);
        String str = new String(res);
        return res;
    }

    /**
     * 计算报文中的len和messageLen
     */
    public void cacularAndSetLen() {
        len = 1 + (payload == null ? 0 : payload.length);
        // 包括len
        messageLen = len + 4;
    }

    public int getMessageLen() {
        return messageLen;
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