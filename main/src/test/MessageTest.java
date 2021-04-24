package test;

import message.ActualMessage;
import message.ShakeHandMessage;

import java.util.Arrays;

public class MessageTest {
    public static void shakeHandTest(String id) {
        ShakeHandMessage msg1 = new ShakeHandMessage();
        msg1.setPeerID(id);
        System.out.println(msg1.toString());
        ShakeHandMessage msg2 = new ShakeHandMessage(msg1.toString().getBytes());
        System.out.println(msg2.getPeerID());
    }

    public static void actualMessageTest(String payload) {
        ActualMessage msg1 = new ActualMessage();
        msg1.setLen(1);
        msg1.setType(2);
        msg1.setPayload(payload.getBytes());
        System.out.println(msg1.toString());
        ActualMessage msg2 = new ActualMessage(msg1.toBytes());
        System.out.println(new String(msg2.getPayload()));
    }

    public static void main(String[] args) {
//        shakeHandTest("0008");
//        shakeHandTest("0080");
//        shakeHandTest("0800");
//        shakeHandTest("8800");
        actualMessageTest("0008");
        actualMessageTest("0088");
        actualMessageTest("08888");
        actualMessageTest("08808");
    }
}