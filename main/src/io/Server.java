package io;

import javafx.beans.binding.MapExpression;
import message.ActualMessage;
import message.ShakeHandMessage;
import peer.LocalPeer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Map;

public class Server extends Thread {
    private static Server server = new Server();
    private Map<Socket, String> invertedSocketMap;

    private Server() {
    }

    public static Server getInstance() {
        return server;
    }

    private SocketChannel socketChannel = null;
    private ByteBuffer buffer;
    private Selector selector = null;

    public void register(String id, String hostName, int port) {
        if (invertedSocketMap.containsValue(id)) {
            return;
        }
        Socket socket = null;
        try {
            socket = new Socket(hostName, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        invertedSocketMap.put(socket, id);
    }

    @Override
    public void run() {
        // TODO
        buffer = ByteBuffer.allocate(1024);
        try {
            socketChannel = SocketChannel.open();
            // TODO
            while (true) {
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

}