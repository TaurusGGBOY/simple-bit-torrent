package io;

import message.ActualMessage;
import message.Message;
import message.ShakeHandMessage;
import util.ByteUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;
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

    public void sendBitfieldMessage(String peerID, String bitfiled) {
        ActualMessage msg = new ActualMessage(ActualMessage.BITFIELD, peerID);
        msg.setPayload(ByteUtil.bitToBytes(bitfiled));
        msg.cacularAndSetLen();
        sendMessage(msg);
    }

    public void sendChokeMessage(String peerID) {
        ActualMessage msg = new ActualMessage(ActualMessage.CHOKE, peerID);
        msg.cacularAndSetLen();
        sendMessage(msg);
    }

    public void sendHaveMessage(String peerID, int index) {
        ActualMessage msg = new ActualMessage(ActualMessage.HAVE, peerID);
        msg.setPayload(ByteUtil.intToByteArray(index));
        msg.cacularAndSetLen();
        sendMessage(msg);
    }

    public void sendInterestedMessage(String peerID) {
        ActualMessage msg = new ActualMessage(ActualMessage.INTERESTED, peerID);
        msg.cacularAndSetLen();
        sendMessage(msg);
    }

    public void sendNotInterestedMessage(String peerID) {
        ActualMessage msg = new ActualMessage(ActualMessage.NOTINTERESTED, peerID);
        msg.cacularAndSetLen();
        sendMessage(msg);
    }

    public void sendPieceMessage(String peerID, int index, byte[] piece) {
        ActualMessage msg = new ActualMessage(ActualMessage.PIECE, peerID);
        byte[] bytes = new byte[4 + piece.length];
        byte[] bytes1 = ByteUtil.intToByteArray(index);
        System.arraycopy(bytes1, 0, bytes, 0, bytes1.length);
        System.arraycopy(piece, 0, bytes, bytes1.length, piece.length);
        msg.setPayload(bytes);
        msg.cacularAndSetLen();
        sendMessage(msg);
    }

    public void sendRequestMessage(String peerID, int index) {
        ActualMessage msg = new ActualMessage(ActualMessage.REQUEST, peerID);
        msg.setPayload(ByteUtil.intToByteArray(index));
        msg.cacularAndSetLen();
        sendMessage(msg);
    }

    public void sendUnchokeMessage(String peerID) {
        ActualMessage msg = new ActualMessage(ActualMessage.UNCHOKE, peerID);
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