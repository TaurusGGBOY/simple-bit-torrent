package peer;

import cfg.CommonCfg;
import cfg.PeerInfoCfg;
import io.IoThread;
import selection.NeighborSelector;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class LocalPeer {
    // TODO 属性的顺序
    public static LinkedHashMap<String, Peer> peers;
    public static String id;
    // TODO 顺序之间是否需要空行

    // TODO 单例的顺序

    private static void listen() {

    }

    public static void main(String[] args) {
        // 读取配置文件
        try {
            CommonCfg.read();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            PeerInfoCfg.read();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // 获取当前的ID
        id = args[0];

        //将配置文件信息写入到local用户，用来添加新连接
        LinkedHashMap<String, Peer> allPeers = PeerInfoCfg.peers;
        Iterator<Map.Entry<String, Peer>> iterator = allPeers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Peer> peer = iterator.next();
            if (peer.getKey().equals(id)) {
                break;
            }
            peers.put(peer.getKey(), peer.getValue());
        }

        // 与这些人建立连接
        // TODO 可以只使用一个socket吗
        for (Map.Entry<String, Peer> entry : peers.entrySet()) {
            Peer peer = entry.getValue();

            // TODO 是否需要合成一个？
            IoThread ioThread = new IoThread(peer.getHostName(), peer.getPort());
            ioThread.start();
            ioThread.shakeHands(id);
            // TODO 发送BitField
        }

        // 开启选举线程
        NeighborSelector neighborSelector = new NeighborSelector();
        neighborSelector.start();

        // 监听后续连接线程
        listen();

    }


}