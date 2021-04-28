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
import java.util.concurrent.ConcurrentHashMap;
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
        socketMap = new ConcurrentHashMap<>();
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

            try {
                Socket socket = socketMap.get(msg.getTo());
                OutputStream stream = socket.getOutputStream();
                stream.write(ByteUtil.concat(ByteUtil.intToByteArray(msg.getMessageLen()), msg.toBytes()));
            } catch (Exception e) {
                // 如果失败 十秒后重试一次
                e.printStackTrace();
                Message finalMsg = msg;
                new Timer("send fail").schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            Socket socket = socketMap.get(finalMsg.getTo());
                            OutputStream stream = socket.getOutputStream();
                            stream.write(ByteUtil.concat(ByteUtil.intToByteArray(finalMsg.getMessageLen()), finalMsg.toBytes()));
                        } catch (IOException ioException) {
                            e.printStackTrace();
                        }
                    }
                }, 10000);
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
        message.cacularAndSetLen();
        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendChokeMessage(String peerID) {
        ActualMessage msg = new ActualMessage(ActualMessage.CHOKE, LocalPeer.id, peerID);
        sendMessage(msg);
    }

    public void sendHaveMessage(String peerID, int index) {
        ActualMessage msg = new ActualMessage(ActualMessage.HAVE, LocalPeer.id, peerID);
        msg.setPayload(ByteUtil.intToByteArray(index));
        sendMessage(msg);
    }

    public void sendInterestedMessage(String peerID) {
        ActualMessage msg = new ActualMessage(ActualMessage.INTERESTED, LocalPeer.id, peerID);
        sendMessage(msg);
    }

    public void sendNotInterestedMessage(String peerID) {
        ActualMessage msg = new ActualMessage(ActualMessage.NOTINTERESTED, LocalPeer.id, peerID);
        sendMessage(msg);
    }

    public void sendPieceMessage(String peerID, int index, byte[] piece) {
        ActualMessage msg = new ActualMessage(ActualMessage.PIECE, LocalPeer.id, peerID);
        byte[] res = new byte[4 + piece.length];
        byte[] indexBytes = ByteUtil.intToByteArray(index);
        System.arraycopy(indexBytes, 0, res, 0, 4);
        System.arraycopy(piece, 0, res, 4, piece.length);

        msg.setPayload(res);

        // 速率++
        LocalPeer.peers.get(peerID).increaseRate();

        sendMessage(msg);
    }

    public void sendRequestMessage(String peerID, int index) {
        ActualMessage msg = new ActualMessage(ActualMessage.REQUEST, LocalPeer.id, peerID);
        msg.setPayload(ByteUtil.intToByteArray(index));
        sendMessage(msg);

        // 20s之后 删除这个键
        new Timer("Request timeout").schedule(new TimerTask() {
            @Override
            public void run() {
                LocalPeer.pieceWaitingMap.remove(index);
            }
        }, 20000);
    }

    public void sendUnchokeMessage(String peerID) {
        ActualMessage msg = new ActualMessage(ActualMessage.UNCHOKE, LocalPeer.id, peerID);
        sendMessage(msg);
    }


    public void sendBitFieldMessage(String peerID) {
        ActualMessage msg = new ActualMessage(ActualMessage.BITFIELD, LocalPeer.id, peerID);
        char[] chars = new char[(int) Math.ceil(CommonCfg.maxPieceNum * 1.0f / 8) * 8];
        Arrays.fill(chars, '0');
        for (int piece : LocalPeer.localUser.pieces) {
            chars[piece] = '1';
        }

        msg.setPayload(ByteUtil.bitStringToByteArr(chars));
        sendMessage(msg);
    }

    public void register(String id, String hostName, int port) throws IOException {
        if (socketMap.containsKey(id)) {
            return;
        }
        Socket socket = new Socket(hostName, port);
        socketMap.put(id, socket);
    }
}