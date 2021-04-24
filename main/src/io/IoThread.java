package io;

import message.ActualMessage;
import message.ShakeHandMessage;
import peer.LocalPeer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class IoThread extends Thread {
    private String host;
    private int port;
    private SocketChannel socketChannel = null;
    private ByteBuffer buffer;
    private Selector selector = null;

    public IoThread(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        buffer = ByteBuffer.allocate(1024);
        try {
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress(host, port));
            while(true){
                if (socketChannel.read(buffer) == -1) {
                    socketChannel.close();
                } else {
                    // 将缓冲器转换为读状态
                    buffer.flip();
                    // 将缓冲器中接收到的值按 localCharset 格式编码保存
                    String receivedRequestData = Charset.forName("UTF-8").newDecoder().decode(buffer).toString();
                    System.out.println(" 接收到客户端的请求数据： " + receivedRequestData);
                    // 返回响应数据给客户端
                    String responseData = " 已接收到你的请求数据，响应数据为： ( 响应数据 )";
                    buffer = ByteBuffer.wrap(responseData.getBytes("UTF-8"));
                    socketChannel.write(buffer);
                    // 关闭通道
                    socketChannel.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(byte[] message) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(message);
        // TODO 是否需要切换？flip 或者 rewind
        socketChannel.write(byteBuffer);
    }

    public void shakeHands(String id) throws IOException {
        ShakeHandMessage message = new ShakeHandMessage();
        message.setPeerID(id);
        sendMessage(message.toBytes());
    }

    public void sendMessage(ActualMessage message) throws IOException {
        sendMessage(message.toBytes());
    }
}