package message;

public abstract class Message {
    public abstract byte[] toBytes();
    public String sendTo;
}