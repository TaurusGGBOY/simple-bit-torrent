package io;

import javafx.beans.binding.MapExpression;
import message.ActualMessage;
import message.ShakeHandMessage;
import messageHandler.ActualMessageHandler;
import messageHandler.ShakeHandMessageHandler;
import peer.LocalPeer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Server extends Thread {
    private static Server server = new Server();
    private ServerSocketChannel listenerChannel; //监听通道
    private Selector selector;//选择器对象
    private static int PORT; //服务器端口

    Map<SocketChannel, String> invertedSocketMap;

    private Server() {
        invertedSocketMap = new HashMap<>();
        PORT = LocalPeer.peers.get(LocalPeer.id).getPort();
        try {
            // 1. 得到监听通道
            listenerChannel = ServerSocketChannel.open();
            // 2. 得到选择器
            selector = Selector.open();
            // 3. 绑定端口
            listenerChannel.bind(new InetSocketAddress(PORT));
            // 4. 设置为非阻塞模式
            listenerChannel.configureBlocking(false);
            // 5. 将选择器绑定到监听通道并监听accept事件
            listenerChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Server getInstance() {
        return server;
    }

    @Override
    public void run() {
        try {
            while (true) { //不停监控
                if (selector.select(1000) == 0) {
                    //此处可编写服务器空闲时的业务代码
                    continue;
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) { //连接请求事件
                        createConnection();
                    }
                    if (key.isReadable()) { //读取数据事件
                        readMessage(key);
                    }
                    //一定要把当前key删掉，防止重复处理
                    iterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register(SocketChannel sc, String id) {
        invertedSocketMap.put(sc, id);
    }

    private void readMessage(SelectionKey key) {
        SocketChannel channel = null;
        try {
            //得到关联的通道
            channel = (SocketChannel) key.channel();
            // TODO 会不会太小了
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int count = channel.read(buffer);
            if (count > 0) {
                String msg = new String(buffer.array());
                if (msg.startsWith(ShakeHandMessage.header)) {
                    ShakeHandMessage shakeHandMessage = new ShakeHandMessage(buffer.array());
                    register(channel, shakeHandMessage.getPeerID());
                    new ShakeHandMessageHandler().handle(shakeHandMessage);
                } else {
                    ActualMessage actualMessage = new ActualMessage(buffer.array());
                    actualMessage.setPeerID(invertedSocketMap.get(channel));
                    new ActualMessageHandler().handle(actualMessage);
                }
            }
        } catch (IOException e) {
            try {
                key.cancel();//取消注册
                channel.close();//关闭通道
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void createConnection() {
        try {
            SocketChannel sc = listenerChannel.accept();
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_READ);
        } catch (Exception e) {
        }
    }


}