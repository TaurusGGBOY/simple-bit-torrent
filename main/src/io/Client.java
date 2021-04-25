package io;

import message.ActualMessage;
import message.Message;
import message.ShakeHandMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client extends Thread {
    private static Client client = new Client();

    private Client() {
    }

    public static Client getInstance() {
        return client;
    }

    private SocketChannel socketChannel = null;
    private ByteBuffer buffer;
    private Selector selector = null;

    private Map<String, Socket> socketMap;
    private BlockingQueue<Message> messageQueue;

    @Override
    public void run() {
        socketMap = new HashMap<>();
        messageQueue = new LinkedBlockingQueue<>();
        // 消息队列中取数据
        while (true) {
            Message msg = null;
            try {
                msg = messageQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (msg instanceof ShakeHandMessage) {
                // 如果是握手消息 则和ShakeHand
                Socket socket = socketMap.get(((ShakeHandMessage) msg).getPeerID());
                try {
                    OutputStream stream = socket.getOutputStream();
                    stream.write(msg.toBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (msg instanceof ActualMessage) {
                Socket socket = socketMap.get(((ActualMessage) msg).getPeerID());
                try {
                    OutputStream stream = socket.getOutputStream();
                    stream.write(msg.toBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("这不是现有数据类型，这不可能！");
            }
        }
    }

    public void shakeHands(String peerID) {
        ShakeHandMessage msg = new ShakeHandMessage();
        msg.setPeerID(peerID);
        sendMessage(msg);
    }

    public void sendMessage(Message message) {
        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendUnchokeMessage(String peerID) {
        ActualMessage msg = new ActualMessage(ActualMessage.UNCHOKE, peerID);
        msg.cacularAndSetLen();
        sendMessage(msg);
    }

    public void sendChokeMessage(String peerID) {
        ActualMessage msg = new ActualMessage(ActualMessage.CHOKE, peerID);
        msg.cacularAndSetLen();
        sendMessage(msg);
    }

    public void sendBitFieldMessage(String peerID) {
        ActualMessage msg = new ActualMessage(ActualMessage.BITFIELD, peerID);
        // TODO
        msg.cacularAndSetLen();
        sendMessage(msg);
    }

    public void register(String id, String hostName, int port) {
        if (socketMap.containsKey(id)) {
            return;
        }
        Socket socket = null;
        try {
            socket = new Socket(hostName, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        socketMap.put(id, socket);
    }
}