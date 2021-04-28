package io;

import message.ActualMessage;
import message.ShakeHandMessage;
import messageHandler.ActualMessageHandler;
import messageHandler.ShakeHandMessageHandler;
import peer.LocalPeer;
import util.ByteUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Server extends Thread {
    private static Server server = new Server();
    //监听通道
    private ServerSocketChannel listenerChannel;
    //选择器对象
    private Selector selector;
    //服务器端口
    private static int PORT;
    // 倒排表，用channel来定位是谁的消息
    Map<SocketChannel, String> invertedSocketMap;

    private Server() {
        invertedSocketMap = new HashMap<>();
        PORT = LocalPeer.localUser.getPort();
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

    /**
     * 单例模式
     *
     * @return
     */
    public static Server getInstance() {
        return server;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if (selector.select(1000) == 0) {
                    //此处可编写服务器空闲时的业务代码
                    continue;
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    //连接请求事件
                    if (key.isAcceptable()) {
                        createConnection();
                    }
                    //读取数据事件
                    if (key.isReadable()) {
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

    /**
     * 将channel放入倒排表
     *
     * @param sc
     * @param id
     */
    public void register(SocketChannel sc, String id) {
        invertedSocketMap.put(sc, id);
    }

    /**
     * 读key中的信息
     *
     * @param key
     */
    private void readMessage(SelectionKey key) {
        SocketChannel channel = null;
        try {
            //得到关联的通道
            channel = (SocketChannel) key.channel();
            // 先读4个字节
            ByteBuffer buffer = ByteBuffer.allocate(4);
            int count = channel.read(buffer);
            if (count > 0) {
                // 此处有粘包问题 已解决
                int len = ByteUtil.byteArrayToInt(buffer.array());
                // 再读len个字节
                buffer = ByteBuffer.allocate(len);
                channel.read(buffer);
                String msg = new String(buffer.array());
                if (msg.startsWith(ShakeHandMessage.header)) {
                    ShakeHandMessage shakeHandMessage = new ShakeHandMessage(buffer.array());
                    register(channel, shakeHandMessage.getFrom());
                    new ShakeHandMessageHandler().handle(shakeHandMessage);
                } else {
                    ActualMessage actualMessage = new ActualMessage(buffer.array());
                    actualMessage.setFrom(invertedSocketMap.get(channel));
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
            e.printStackTrace();
        }
    }

    /**
     * 创建nio连接
     */
    private void createConnection() {
        try {
            SocketChannel sc = listenerChannel.accept();
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_READ);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}