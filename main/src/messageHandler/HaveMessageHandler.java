package messageHandler;

import io.Client;
import message.ActualMessage;
import peer.LocalPeer;
import util.ByteUtil;

import java.util.Set;

public class HaveMessageHandler {
    public void handle(ActualMessage msg) {
        int index = ByteUtil.byteArrayToInt(msg.getPayload());

        // 更新该人的set
        Set<Integer> pieces = LocalPeer.peers.get(msg.getPeerID()).pieces;
        pieces.add(index);

        // 如果有感兴趣的就发送感兴趣
        if (!LocalPeer.localUser.pieces.contains(index)) {
            Client.getInstance().sendInterestedMessage(msg.getPeerID());
        }
    }
}