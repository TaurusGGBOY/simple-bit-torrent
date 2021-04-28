package message;

public abstract class Message {
    public abstract byte[] toBytes();

    protected String from;
    protected String to;
    // messageLen以后内容的长度
    protected int messageLen;

    public abstract int getMessageLen();

    public abstract String getTo();

    public abstract String getFrom();

    public abstract void setTo(String to);

    public abstract void setFrom(String form);

    public abstract void cacularAndSetLen();
}