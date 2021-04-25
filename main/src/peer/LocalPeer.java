package peer;

import cfg.CommonCfg;
import cfg.PeerInfoCfg;
import io.Client;
import io.Server;
import selection.NeighborSelector;

import java.io.IOException;
import java.util.*;

public class LocalPeer {
    // TODO 属性的顺序
    public static LinkedHashMap<String, Peer> peers;
    public static String id;
    public static Peer localUser;
    public static Map<Integer, String> pieceWaitingMap;
    // TODO 顺序之间是否需要空行

    // TODO 单例的顺序

    public static void main(String[] args) {
        // 读取配置文件
        try {
            CommonCfg.read();
            PeerInfoCfg.read();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // 获取自己
        localUser = peers.get(id);

        // 初始化等待队列
        pieceWaitingMap = new HashMap<>();


        // 如果有完整文件就将所有分片加入到自己的集合
        if (localUser.isHasFileOrNot()) {
            for (int i = 0; i < CommonCfg.maxPieceNum; i++) {
                localUser.pieces.add(i);
            }
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

        // 启动本地监听服务器
        Server server = Server.getInstance();
        server.start();
        Client client = Client.getInstance();
        client.start();

        // 与这些人建立连接
        for (Map.Entry<String, Peer> entry : peers.entrySet()) {
            Peer peer = entry.getValue();
            // 发送客户端注册
            String PeerID = entry.getKey();
            client.register(PeerID, peer.getHostName(), peer.getPort());
            // 发送握手信息
            client.shakeHands(PeerID);
        }

        // 开启选举线程
        NeighborSelector neighborSelector = new NeighborSelector(CommonCfg.unchokingInterval, CommonCfg.optimisticUnchokingInterval, CommonCfg.numberOfPreferredNeighbors);
        neighborSelector.start();
    }


}