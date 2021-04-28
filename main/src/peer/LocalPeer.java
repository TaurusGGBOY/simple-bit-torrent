package peer;

import cfg.CommonCfg;
import cfg.PeerInfoCfg;
import file.PieceFile;
import io.Client;
import io.Server;
import selection.NeighborSelector;
import log.Logger;

import java.io.IOException;
import java.util.*;

public class LocalPeer {
    public static LinkedHashMap<String, Peer> peers = new LinkedHashMap<>();
    public static String id;
    public static Peer localUser;
    public static Map<Integer, String> pieceWaitingMap = new HashMap<>();
    public static Set<String> shakingHands = new HashSet<>();
    public static Set<String> bitfielding = new HashSet<>();

    public static void main(String[] args) throws IOException {
        // 读取配置文件
        try {
            CommonCfg.read();
            PeerInfoCfg.read();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


        // 获取当前的ID
        id = args[0];

        // 获取自己
        localUser = PeerInfoCfg.peers.get(id);

        // 初始化设置logger id
        Logger.id = id;

        // 删除文件夹
        PieceFile.removeDirById(id);

        // 创建Logger文件夹
        Logger.createLogFile();

        // 初始化等待队列
        pieceWaitingMap = new HashMap<>();

        // 如果有完整文件就将所有分片加入到自己的集合
        if (localUser.isHasFileOrNot()) {
            // 将文件分开
            PieceFile.spilt("./main/" + CommonCfg.fileName, CommonCfg.maxPieceNum, CommonCfg.pieceSize, id);

            // piece填充
            for (int i = 0; i < CommonCfg.maxPieceNum; i++) {
                localUser.pieces.add(i);
            }
        }

        // 启动本地监听服务器
        Server server = Server.getInstance();
        server.start();
        Client client = Client.getInstance();
        client.start();

        //将配置文件信息写入到local用户，用来添加新连接
        LinkedHashMap<String, Peer> allPeers = PeerInfoCfg.peers;
        Iterator<Map.Entry<String, Peer>> iterator = allPeers.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Peer> peer = iterator.next();
            if (peer.getKey().equals(id)) {
                break;
            }
            try {
                client.register(peer.getKey(), peer.getValue().getHostName(), peer.getValue().getPort());
                // 发送握手信息
                client.shakeHands(peer.getKey());
            } catch (Exception e) {
                // 如果失败 十秒后重试一次
                new Timer("Socket Create").schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            client.register(peer.getKey(), peer.getValue().getHostName(), peer.getValue().getPort());
                            // 发送握手信息
                            client.shakeHands(peer.getKey());
                        } catch (IOException ioException) {
                        }
                    }
                }, 10000);
                e.printStackTrace();
            }
        }

        // 开启选举线程
        NeighborSelector neighborSelector = new NeighborSelector(CommonCfg.unchokingInterval, CommonCfg.optimisticUnchokingInterval, CommonCfg.numberOfPreferredNeighbors);
        neighborSelector.start();
    }

    public static void checkFinish() {
        for (Peer peer : peers.values()) {
            if (peer.pieces.size() < CommonCfg.maxPieceNum) {
                return;
            }
        }
        if (localUser.pieces.size() < CommonCfg.maxPieceNum) {
            return;
        }
        Logger.finish(localUser.getID());

        PieceFile.merge("cpabe.rar", CommonCfg.maxPieceNum, localUser.getID());
        System.exit(0);
    }


}