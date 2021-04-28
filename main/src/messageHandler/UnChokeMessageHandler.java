package messageHandler;

import io.Client;
import message.ActualMessage;
import peer.LocalPeer;
import peer.Peer;
import log.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UnChokeMessageHandler {
    /**
     * @param msg
     */
    public void handle(ActualMessage msg) {
        Peer remotePeer = LocalPeer.peers.get(msg.getFrom());
        remotePeer.setChoke(false);

        Logger.receiveUnchoke(LocalPeer.id, remotePeer.getID());

        // 是否还有感兴趣的片段
        List<Integer> list = new ArrayList<>();
        for (int piece : remotePeer.pieces) {
            // 保证等待队列没有才加入
            if (!LocalPeer.localUser.pieces.contains(piece) && !LocalPeer.pieceWaitingMap.containsKey(piece)) {
                list.add(piece);
            }
        }
        // 如果没有想要的就直接返回就好
        if (list.size() <= 0) {
            return;
        }
        // 如果有就随机选择一片请求
        int random = new Random().nextInt(list.size());
        Client.getInstance().sendRequestMessage(msg.getFrom(), list.get(random));

    }
}