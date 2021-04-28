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

    // 方便使用id查Socket
    private Map<String, Socket> socketMap;
    // 消息队列
    private BlockingQueue<Message> messageQueue;

    private Client() {
        socketMap = new ConcurrentHashMap<>();
        messageQueue = new LinkedBlockingQueue<>();
    }

    public static Client getInstance() {
        return client;
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

    /**
     * 往消息队列中放入消息
     * @param message
     */
    public void sendMessage(Message message) {
        // 在发送之前先计算length
        message.cacularAndSetLen();

        try {
            messageQueue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 握手，同时会记录握手事件
     * @param peerID
     */
    public void shakeHands(String peerID) {
        Logger.makeConnection(LocalPeer.id, peerID);
        sendShakeHandMessage(peerID);
        LocalPeer.shakingHands.add(peerID);
    }

    /**
     * 发送握手消息
     * @param peerID
     */
    public void sendShakeHandMessage(String peerID) {
        ShakeHandMessage msg = new ShakeHandMessage();
        msg.setFrom(LocalPeer.id);
        msg.setTo(peerID);
        sendMessage(msg);
    }

    /**
     * 发送Choke消息
     * @param peerID
     */
    public void sendChokeMessage(String peerID) {
        ActualMessage msg = new ActualMessage(ActualMessage.CHOKE, LocalPeer.id, peerID);
        sendMessage(msg);
    }

    /**
     * 发送Have消息
     * @param peerID
     * @param index
     */
    public void sendHaveMessage(String peerID, int index) {
        ActualMessage msg = new ActualMessage(ActualMessage.HAVE, LocalPeer.id, peerID);
        msg.setPayload(ByteUtil.intToByteArray(index));
        sendMessage(msg);
    }

    /**
     * 发送Interested消息
     * @param peerID
     */
    public void sendInterestedMessage(String peerID) {
        ActualMessage msg = new ActualMessage(ActualMessage.INTERESTED, LocalPeer.id, peerID);
        sendMessage(msg);
    }

    /**
     * 发送NotInterested消息
     * @param peerID
     */
    public void sendNotInterestedMessage(String peerID) {
        ActualMessage msg = new ActualMessage(ActualMessage.NOTINTERESTED, LocalPeer.id, peerID);
        sendMessage(msg);
    }

    /**
     * 发送Piece消息
     * @param peerID
     * @param index
     * @param piece
     */
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

    /**
     * 发送请求消息
     * @param peerID
     * @param index
     */
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

    /**
     * 发送Unchoke消息
     * @param peerID
     */
    public void sendUnchokeMessage(String peerID) {
        ActualMessage msg = new ActualMessage(ActualMessage.UNCHOKE, LocalPeer.id, peerID);
        sendMessage(msg);
    }

    /**
     * 发送BitField消息
     * @param peerID
     */
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

    /**
     * 注册id，申请套接字，并保存
     * @param id
     * @param hostName
     * @param port
     * @throws IOException
     */
    public void register(String id, String hostName, int port) throws IOException {
        if (socketMap.containsKey(id)) {
            return;
        }
        Socket socket = new Socket(hostName, port);
        socketMap.put(id, socket);
    }
}