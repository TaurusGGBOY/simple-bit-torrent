package io;

import cfg.CommonCfg;
import message.ActualMessage;
import message.Message;
import message.ShakeHandMessage;
import peer.LocalPeer;
import util.ByteUtil;
import log.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client extends Thread {
    private static Client client = new Client();

    public static Client getInstance() {
        return client;
    }

    private SocketChannel socketChannel = null;
    private ByteBuffer buffer;
    private Selector selector = null;

    private Map<String, Socket> socketMap;
    private BlockingQueue<Message> messageQueue;

    private Client() {
        socketMap = new HashMap<>();
        messageQueue = new LinkedBlockingQueue<>();
    }

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
                Socket socket = socketMap.get(((ShakeHandMessage) msg).getTo());
                try {
                    OutputStream stream = socket.getOutputStream();
                    stream.write(msg.toBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (msg instanceof ActualMessage) {
                Socket socket = socketMap.get(((ActualMessage) msg).getTo());
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
        Logger.makeConnection(LocalPeer.id, peerID);
        sendShakeHandMessage(peerID);
        LocalPeer.shakingHands.add(peerID);
    }

    public void sendShakeHandMessage(String peerID) {
        ShakeHandMessage msg = new ShakeHandMessage();
        msg.setFrom(LocalPeer.id);
        msg.setTo(peerID);
        sendMessage(msg);
    }

    public void sendMessage(Message message) {
        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendChokeMessage(String peerID) {
        ActualMessage msg = new ActualMessage(ActualMessage.CHOKE, LocalPeer.id, peerID);
        msg.cacularAndSetLen();
        sendMessage(msg);
    }

    public void sendHaveMessage(String peerID, int index) {
        ActualMessage msg = new ActualMessage(ActualMessage.HAVE, LocalPeer.id, peerID);
        msg.setPayload(ByteUtil.intToByteArray(index));
        msg.cacularAndSetLen();
        sendMessage(msg);
    }

    public void sendInterestedMessage(String peerID) {
        ActualMessage msg = new ActualMessage(ActualMessage.INTERESTED, LocalPeer.id, peerID);
        msg.cacularAndSetLen();
        sendMessage(msg);
    }

    public void sendNotInterestedMessage(String peerID) {
        ActualMessage msg = new ActualMessage(ActualMessage.NOTINTERESTED, LocalPeer.id, peerID);
        msg.cacularAndSetLen();
        sendMessage(msg);
    }

    public void sendPieceMessage(String peerID, int index, byte[] piece) {
        ActualMessage msg = new ActualMessage(ActualMessage.PIECE, LocalPeer.id, peerID);
        byte[] res = new byte[4 + piece.length];
        byte[] indexBytes = ByteUtil.intToByteArray(index);
        System.arraycopy(indexBytes, 0, res, 0, 4);
        System.arraycopy(piece, 0, res, 4, piece.length);
        msg.setPayload(res);
        msg.cacularAndSetLen();
        sendMessage(msg);
    }

    public void sendRequestMessage(String peerID, int index) {
        ActualMessage msg = new ActualMessage(ActualMessage.REQUEST, LocalPeer.id, peerID);
        msg.setPayload(ByteUtil.intToByteArray(index));
        msg.cacularAndSetLen();
        sendMessage(msg);
    }

    public void sendUnchokeMessage(String peerID) {
        ActualMessage msg = new ActualMessage(ActualMessage.UNCHOKE, LocalPeer.id, peerID);
        msg.cacularAndSetLen();
        sendMessage(msg);
    }


    public void sendBitFieldMessage(String peerID) {
        ActualMessage msg = new ActualMessage(ActualMessage.BITFIELD, LocalPeer.id, peerID);
        // TODO 转换成bitfield不对
        char[] chars = new char[(int) Math.ceil(CommonCfg.maxPieceNum * 1.0f / 8) * 8];
        Arrays.fill(chars, '0');
        for (int piece : LocalPeer.localUser.pieces) {
            chars[piece] = '1';
        }

        msg.setPayload(ByteUtil.bitStringToByteArr(chars));
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